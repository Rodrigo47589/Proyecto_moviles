package movil.proyecto_moviles.screens.mapa

import movil.proyecto_moviles.data.mapa.NominatimResult

sealed class MapaEvent {
    data class OnParkingSelected(val parking: ParkingUi) : MapaEvent()
    data object OnClearSelection : MapaEvent()
    data class OnSearchChanged(val value: String) : MapaEvent()
    data class OnUserLocationResolved(val latitude: Double, val longitude: Double) : MapaEvent()

    data class OnDestinationSelected(val result: movil.proyecto_moviles.data.mapa.NominatimResult) : MapaEvent()
    data object OnRefreshNearby : MapaEvent()

    object CalcularRutaEvent : MapaEvent()

    // NUEVO: autocompletado de destino
    data class OnDestinationQueryChanged(val value: String) : MapaEvent()
    data class OnSuggestionSelected(val suggestion: movil.proyecto_moviles.data.mapa.NominatimResult) : MapaEvent()
    object OnClearDestination : MapaEvent()
}