package movil.proyecto_moviles.screens.mapa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import movil.proyecto_moviles.data.mapa.BikeRouteRepository
import movil.proyecto_moviles.data.mapa.ParkingRepository
import movil.proyecto_moviles.data.mapa.SupabaseBikeRouteRepository
import movil.proyecto_moviles.data.mapa.SupabaseParkingRepository

class MapaViewModel : ViewModel() {

    private val parkingRepository: ParkingRepository = SupabaseParkingRepository()
    private val bikeRouteRepository: BikeRouteRepository = SupabaseBikeRouteRepository()

    private val _uiState = MutableStateFlow(
        MapaUiState(
            loading = true,
            userLocation = null
        )
    )
    val uiState: StateFlow<MapaUiState> = _uiState.asStateFlow()

    init {
        loadBikeRoutes()
    }

    private fun loadBikeRoutes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                loading = true,
                error = null
            )

            runCatching {
                bikeRouteRepository.getBikeRoutes()
            }.onSuccess { routes ->
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    bikeRoutes = routes
                )
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
            _uiState.value = _uiState.value.copy(
                loading = true,
                error = null
            )

            runCatching {
                parkingRepository.getNearbyParkings(
                    userLat = location.latitude,
                    userLng = location.longitude,
                    radiusM = _uiState.value.radiusMeters,
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

    fun onEvent(event: MapaEvent) {
        when (event) {
            is MapaEvent.OnParkingSelected -> {
                _uiState.value = _uiState.value.copy(
                    selectedParking = event.parking
                )
            }

            MapaEvent.OnClearSelection -> {
                _uiState.value = _uiState.value.copy(
                    selectedParking = null
                )
            }

            is MapaEvent.OnSearchChanged -> {
                _uiState.value = _uiState.value.copy(
                    searchText = event.value
                )
                loadNearbyParkings()
            }

            is MapaEvent.OnUserLocationResolved -> {
                _uiState.value = _uiState.value.copy(
                    userLocation = UserLocationUi(
                        latitude = event.latitude,
                        longitude = event.longitude
                    )
                )
                loadNearbyParkings()
            }

            MapaEvent.OnRefreshNearby -> {
                loadNearbyParkings()
            }
        }
    }
}