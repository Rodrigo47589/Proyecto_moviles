package movil.proyecto_moviles.screens.mapa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import movil.proyecto_moviles.data.mapa.BikeGraphRouter
import movil.proyecto_moviles.data.mapa.BikeRouteRepository
import movil.proyecto_moviles.data.mapa.NominatimResult
import movil.proyecto_moviles.data.mapa.NominatimService
import movil.proyecto_moviles.data.mapa.OsrmService
import movil.proyecto_moviles.data.mapa.ParkingRepository
import movil.proyecto_moviles.data.mapa.SupabaseBikeRouteRepository
import movil.proyecto_moviles.data.mapa.SupabaseParkingRepository

class MapaViewModel : ViewModel() {

    private val parkingRepository: ParkingRepository = SupabaseParkingRepository()
    private val bikeRouteRepository: BikeRouteRepository = SupabaseBikeRouteRepository()
    private val nominatimService = NominatimService()
    private val osrmService = OsrmService()

    private var bikeGraphRouter: BikeGraphRouter? = null

    private val VELOCIDAD_BICI_M_S = 4.2 // ~15 km/h promedio
    private val UMBRAL_ENTRADA_RED_METROS = 25.0

    private val _uiState = MutableStateFlow(
        MapaUiState(
            loading = true,
            userLocation = null
        )
    )
    val uiState: StateFlow<MapaUiState> = _uiState.asStateFlow()

    private var destinationSearchJob: Job? = null
    private var calcularRutaJob: Job? = null

    init {
        loadBikeRoutes()
    }

    private fun loadBikeRoutes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null)

            runCatching {
                bikeRouteRepository.getBikeRoutes()
            }.onSuccess { routes ->
                // CORRECCIÓN: Se procesa la construcción del grafo en el hilo Dispatchers.Default
                // para evitar que la aplicación se congele o muestre "No responde" al inicio.
                val router = withContext(Dispatchers.Default) {
                    BikeGraphRouter(routes)
                }
                bikeGraphRouter = router
                _uiState.value = _uiState.value.copy(loading = false, bikeRoutes = routes)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = e.message ?: "Error cargando ciclovías"
                )
            }
        }
    }

    private fun loadNearbyParkings() {
        val location = _uiState.value.userLocation ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null)

            runCatching {
                parkingRepository.getNearbyParkings(
                    userLat = location.latitude,
                    userLng = location.longitude,
                    radiusM = _uiState.value.radiusMeters.toInt(), // CORRECCIÓN: Conversión explícita a Int
                    searchText = _uiState.value.searchText.ifBlank { null }
                )
            }.onSuccess { parkings ->
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    nearbyParkings = parkings,
                    selectedParking = parkings.firstOrNull()
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = e.message ?: "Error cargando estacionamientos"
                )
            }
        }
    }

    private fun onDestinationQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(destinationQuery = query)

        destinationSearchJob?.cancel()

        if (query.length < 3) {
            _uiState.value = _uiState.value.copy(destinationSuggestions = emptyList())
            return
        }

        destinationSearchJob = viewModelScope.launch {
            delay(400)
            val resultados = nominatimService.buscarLugar(query)
            _uiState.value = _uiState.value.copy(destinationSuggestions = resultados)
        }
    }

    private fun onDestinationSelected(result: NominatimResult) {
        val lat = result.lat.toDoubleOrNull() ?: return
        val lon = result.lon.toDoubleOrNull() ?: return

        _uiState.value = _uiState.value.copy(
            destinationQuery = result.display_name,
            destinationName = result.display_name,
            destinationLocation = UserLocationUi(latitude = lat, longitude = lon),
            destinationSuggestions = emptyList()
        )

        calcularRuta()
    }

    private fun clearDestination() {
        destinationSearchJob?.cancel()
        calcularRutaJob?.cancel()
        _uiState.value = _uiState.value.copy(
            destinationQuery = "",
            destinationName = null,
            destinationLocation = null,
            destinationSuggestions = emptyList(),
            routeDistanceMeters = null,
            routeDurationSeconds = null,
            routeSegments = emptyList() // CORRECCIÓN: Limpieza sincronizada al nuevo estado
        )
    }

    private fun calcularRuta() {
        val origen = _uiState.value.userLocation ?: return
        val destino = _uiState.value.destinationLocation ?: return
        val router = bikeGraphRouter

        calcularRutaJob?.cancel()
        calcularRutaJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loadingRoute = true, error = null)

            val origenPunto = BikeRoutePointUi(origen.latitude, origen.longitude)
            val destinoPunto = BikeRoutePointUi(destino.latitude, destino.longitude)

            val segments = mutableListOf<RouteSegmentUi>()
            var distanciaTotal = 0.0
            var duracionTotal = 0.0
            var usoRed = false

            if (router != null) {
                // AQUÍ OCURRE LA MAGIA: Nos devuelve el camino y la puerta de entrada ideal
                val bestPathResult = router.findBestPath(origenPunto, destinoPunto)

                if (bestPathResult != null) {
                    usoRed = true
                    val caminoBici = bestPathResult.first
                    val nodoEntrada = bestPathResult.second
                    val nodoSalida = router.nearestNode(destinoPunto)!!

                    // TRAMO 1 (OSRM): Desde SJM hasta la puerta de entrada de la ciclovía
                    val distEntrada = router.distanceToNode(origenPunto, nodoEntrada)
                    if (distEntrada > UMBRAL_ENTRADA_RED_METROS) {
                        val rutaCalle1 = osrmService.obtenerRuta(
                            origen.latitude, origen.longitude,
                            nodoEntrada.lat, nodoEntrada.lon
                        )
                        if (rutaCalle1 != null) {
                            segments.add(
                                RouteSegmentUi(
                                    rutaCalle1.geometry.coordinates.toPuntos(),
                                    RouteSegmentType.STREET
                                )
                            )
                            distanciaTotal += rutaCalle1.distance
                            duracionTotal += rutaCalle1.distance / VELOCIDAD_BICI_M_S
                        }
                    }

                    // TRAMO 2 (CICLOVÍA): El recorrido seguro por Miraflores
                    if (caminoBici.size >= 2) {
                        segments.add(RouteSegmentUi(caminoBici, RouteSegmentType.BIKE_LANE))
                        val distBici = router.pathDistanceMeters(caminoBici)
                        distanciaTotal += distBici
                        duracionTotal += distBici / VELOCIDAD_BICI_M_S
                    }

                    // TRAMO 3 (OSRM): Desde donde termina la ciclovía hasta la puerta de tu destino
                    val distSalida = router.distanceToNode(destinoPunto, nodoSalida)
                    if (distSalida > UMBRAL_ENTRADA_RED_METROS) {
                        val rutaCalle2 = osrmService.obtenerRuta(
                            nodoSalida.lat, nodoSalida.lon,
                            destino.latitude, destino.longitude
                        )
                        if (rutaCalle2 != null) {
                            segments.add(
                                RouteSegmentUi(
                                    rutaCalle2.geometry.coordinates.toPuntos(),
                                    RouteSegmentType.STREET
                                )
                            )
                            distanciaTotal += rutaCalle2.distance
                            duracionTotal += rutaCalle2.distance / VELOCIDAD_BICI_M_S
                        }
                    }
                }
            }

            // Plan B de emergencia (si algo falla catastróficamente)
            if (!usoRed) {
                val rutaCompleta = osrmService.obtenerRuta(
                    origen.latitude, origen.longitude,
                    destino.latitude, destino.longitude
                )
                if (rutaCompleta != null) {
                    segments.add(
                        RouteSegmentUi(
                            rutaCompleta.geometry.coordinates.toPuntos(),
                            RouteSegmentType.STREET
                        )
                    )
                    distanciaTotal = rutaCompleta.distance
                    duracionTotal = rutaCompleta.distance / VELOCIDAD_BICI_M_S
                }
            }

            if (segments.isNotEmpty()) {
                _uiState.value = _uiState.value.copy(
                    loadingRoute = false,
                    routeDistanceMeters = distanciaTotal,
                    routeDurationSeconds = duracionTotal,
                    routeSegments = segments
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    loadingRoute = false,
                    error = "No se pudo calcular la ruta"
                )
            }
        }
    }

    fun onEvent(event: MapaEvent) {
        when (event) {
            is MapaEvent.OnParkingSelected -> {
                _uiState.value = _uiState.value.copy(selectedParking = event.parking)
            }

            MapaEvent.OnClearSelection -> {
                _uiState.value = _uiState.value.copy(selectedParking = null)
            }

            is MapaEvent.OnSearchChanged -> {
                _uiState.value = _uiState.value.copy(searchText = event.value)
                loadNearbyParkings()
            }

            is MapaEvent.OnSuggestionSelected -> {
                onDestinationSelected(event.suggestion)
            }

            is MapaEvent.OnUserLocationResolved -> {
                _uiState.value = _uiState.value.copy(
                    userLocation = UserLocationUi(
                        latitude = event.latitude,
                        longitude = event.longitude
                    )
                )
                loadNearbyParkings()
                if (_uiState.value.destinationLocation != null) {
                    calcularRuta()
                }
            }

            MapaEvent.OnRefreshNearby -> {
                loadNearbyParkings()
            }

            MapaEvent.CalcularRutaEvent -> calcularRuta()

            is MapaEvent.OnDestinationQueryChanged -> onDestinationQueryChanged(event.value)
            is MapaEvent.OnDestinationSelected -> onDestinationSelected(event.result)
            MapaEvent.OnClearDestination -> clearDestination()
        }
    }
}

fun List<List<Double>>.toPuntos(): List<BikeRoutePointUi> {
    return this.mapNotNull { coord ->
        if (coord.size >= 2) {
            BikeRoutePointUi(latitude = coord[1], longitude = coord[0])
        } else null
    }
}