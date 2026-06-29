package movil.proyecto_moviles.screens.mapa

sealed class MapaEvent {
    data class OnParkingSelected(val parking: ParkingUi) : MapaEvent()
    data object OnClearSelection : MapaEvent()
    data class OnSearchChanged(val value: String) : MapaEvent()
    data class OnUserLocationResolved(val latitude: Double, val longitude: Double) : MapaEvent()
    data object OnRefreshNearby : MapaEvent()
}