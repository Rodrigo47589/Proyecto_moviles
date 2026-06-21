package movil.proyecto_moviles.screens.mapa

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import movil.proyecto_moviles.screens.mapa.components.MapAreaPlaceholder
import movil.proyecto_moviles.screens.mapa.components.NearbyParkingSheet

@Composable
fun MapaScreen(
    viewModel: MapaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F5FB))
    ) {
        MapAreaPlaceholder(
            selectedParking = uiState.selectedParking,
            modifier = Modifier.fillMaxSize()
        )

        NearbyParkingSheet(
            parkings = uiState.nearbyParkings,
            onParkingClick = { parking ->
                viewModel.onEvent(MapaEvent.OnParkingSelected(parking))
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(230.dp)
        )
    }
}