package movil.proyecto_moviles.screens.mapa

data class MapaUiState(
    val loading: Boolean = false,
    val searchText: String = "",
    val userLocation: UserLocationUi? = null,
    val radiusMeters: Int = 1500,
    val nearbyParkings: List<ParkingUi> = emptyList(),
    val bikeRoutes: List<BikeRouteUi> = emptyList(),
    val selectedParking: ParkingUi? = null,
    val error: String? = null
)