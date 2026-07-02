package movil.proyecto_moviles.screens.mapa

data class MapaUiState(
    val loading: Boolean = false,
    val userLocation: UserLocationUi? = null,
    val destinationQuery: String = "",
    val destinationName: String? = null,
    val destinationLocation: UserLocationUi? = null,
    val destinationSuggestions: List<movil.proyecto_moviles.data.mapa.NominatimResult> = emptyList(),
    val routeDistanceMeters: Double? = null,
    val routeDurationSeconds: Double? = null,

    // CORRECCIÓN: ahora usamos los segmentos de ruta
    val routeSegments: List<RouteSegmentUi> = emptyList(),

    val bikeRoutes: List<BikeRouteUi> = emptyList(),
    val nearbyParkings: List<ParkingUi> = emptyList(),
    val selectedParking: ParkingUi? = null,
    val searchText: String = "",
    val radiusMeters: Double = 2000.0,
    val loadingRoute: Boolean = false,
    val error: String? = null
)