package movil.proyecto_moviles.screens.mapa

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import movil.proyecto_moviles.data.mapa.OpenStreetMapView
import movil.proyecto_moviles.screens.mapa.components.MapAreaPlaceholder
import movil.proyecto_moviles.screens.mapa.components.NearbyParkingSheet

@Composable
fun MapaScreen(
    viewModel: MapaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineGranted || coarseGranted) {
            obtenerUbicacionActual(
                fusedLocationClient = fusedLocationClient,
                onLocationReceived = { lat, lng ->
                    viewModel.onEvent(
                        MapaEvent.OnUserLocationResolved(lat, lng)
                    )
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        val finePermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarsePermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (
            finePermission == PackageManager.PERMISSION_GRANTED ||
            coarsePermission == PackageManager.PERMISSION_GRANTED
        ) {
            obtenerUbicacionActual(
                fusedLocationClient = fusedLocationClient,
                onLocationReceived = { lat, lng ->
                    viewModel.onEvent(
                        MapaEvent.OnUserLocationResolved(lat, lng)
                    )
                }
            )
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F5FB))
    ) {
        OpenStreetMapView(
            bikeRoutes = uiState.bikeRoutes,
            parkings = uiState.nearbyParkings,
            userLocation = uiState.userLocation,
            modifier = Modifier.fillMaxSize()

        )

        when {
            uiState.loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            uiState.error != null -> {
                Text(
                    text = uiState.error ?: "Error",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                )
            }

            uiState.userLocation == null -> {
                Text(
                    text = "Esperando permiso o ubicación actual",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 24.dp),
                    color = Color(0xFF4B5563)
                )
            }

            uiState.nearbyParkings.isEmpty() -> {
                Text(
                    text = "No se encontraron estacionamientos cercanos",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 24.dp),
                    color = Color(0xFF4B5563)
                )
            }
        }

        NearbyParkingSheet(
            parkings = uiState.nearbyParkings,
            onParkingClick = { parking ->
                viewModel.onEvent(MapaEvent.OnParkingSelected(parking))
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(380.dp)
        )
    }
}

@SuppressLint("MissingPermission")
private fun obtenerUbicacionActual(
    fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient,
    onLocationReceived: (Double, Double) -> Unit
) {
    fusedLocationClient.lastLocation
        .addOnSuccessListener { location ->
            if (location != null) {
                onLocationReceived(location.latitude, location.longitude)
            }
        }
}