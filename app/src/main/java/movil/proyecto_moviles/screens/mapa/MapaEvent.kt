package movil.proyecto_moviles.screens.mapa

sealed class MapaEvent {
    data class OnParkingSelected(val parking: ParkingUi) : MapaEvent()
    data object OnClearSelection : MapaEvent()
}