package movil.proyecto_moviles.screens.mapa

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import movil.proyecto_moviles.data.mapa.FakeParkingRepository
import movil.proyecto_moviles.data.mapa.ParkingRepository

class MapaViewModel : ViewModel() {

    private val repository: ParkingRepository = FakeParkingRepository()

    private val _uiState = MutableStateFlow(
        MapaUiState(
            nearbyParkings = repository.getNearbyParkings()
        )
    )
    val uiState: StateFlow<MapaUiState> = _uiState.asStateFlow()

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
        }
    }
}