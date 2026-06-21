package movil.proyecto_moviles.screens.mapa

data class MapaUiState(
    val loading: Boolean = false,
    val searchText: String = "Miraflores, Lima",
    val nearbyParkings: List<ParkingUi> = emptyList(),
    val selectedParking: ParkingUi? = null,
    val error: String? = null
)