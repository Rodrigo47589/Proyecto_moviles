package movil.proyecto_moviles.screens.mapa

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import movil.proyecto_moviles.data.mapa.OpenStreetMapView
import movil.proyecto_moviles.screens.mapa.components.NearbyParkingSheet
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaScreen(
    viewModel: MapaViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // Variables de estado para el nombre del distrito
    var nombreDistrito by remember { mutableStateOf("Buscando área...") }
    var centroMapa by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    val geocoder = remember { Geocoder(context, Locale("es", "PE")) }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = true
        )
    )

    // Efecto para traducir las coordenadas a distrito cuando el mapa se mueve
    LaunchedEffect(centroMapa) {
        centroMapa?.let { (lat, lon) ->
            // Espera medio segundo (debounce) para no saturar al geocoder mientras arrastras
            delay(500)

            // Lo mandamos a un hilo de fondo para que no congele la pantalla
            withContext(Dispatchers.IO) {
                try {
                    val direcciones = geocoder.getFromLocation(lat, lon, 1)
                    if (!direcciones.isNullOrEmpty()) {
                        val direccion = direcciones[0]
                        // En Lima, los distritos suelen venir mapeados como "subLocality" o "locality"
                        val distritoDetectado = direccion.subLocality ?: direccion.locality ?: "Lima"
                        nombreDistrito = distritoDetectado
                    }
                } catch (e: Exception) {
                    // Ignora si no hay internet o falla el geocoder
                }
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineGranted || coarseGranted) {
            obtenerUbicacionActual(
                fusedLocationClient = fusedLocationClient,
                onLocationReceived = { lat, lng ->
                    viewModel.onEvent(MapaEvent.OnUserLocationResolved(lat, lng))
                    if (centroMapa == null) centroMapa = Pair(lat, lng) // Centramos la primera vez
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        val finePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarsePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (finePermission == PackageManager.PERMISSION_GRANTED || coarsePermission == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacionActual(
                fusedLocationClient = fusedLocationClient,
                onLocationReceived = { lat, lng ->
                    viewModel.onEvent(MapaEvent.OnUserLocationResolved(lat, lng))
                    if (centroMapa == null) centroMapa = Pair(lat, lng)
                }
            )
        } else {
            locationPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 110.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContainerColor = Color.White,
        sheetShadowElevation = 16.dp,
        sheetDragHandle = null,
        sheetContent = {
            NearbyParkingSheet(
                parkings = uiState.nearbyParkings,
                onParkingClick = { parking ->
                    viewModel.onEvent(MapaEvent.OnParkingSelected(parking))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF2F5FB))
        ) {
            OpenStreetMapView(
                bikeRoutes = uiState.bikeRoutes,
                parkings = uiState.nearbyParkings,
                userLocation = uiState.userLocation,
                // Recibimos las coordenadas desde el mapa
                onMapCenterChange = { lat, lon ->
                    centroMapa = Pair(lat, lon)
                },
                modifier = Modifier.fillMaxSize()
            )

            when {
                uiState.loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    Text(
                        text = uiState.error ?: "Error",
                        modifier = Modifier.align(Alignment.Center).padding(24.dp)
                    )
                }
            }

            // CAPA 2: INDICADOR DINÁMICO DE DISTRITO
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, start = 16.dp, end = 16.dp)
                    .align(Alignment.TopCenter),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Ubicación",
                        tint = Color(0xFF0A6BEA)
                    )
                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = nombreDistrito, // ¡Este texto cambiará cuando muevas el mapa!
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // CAPA 3: LEYENDA (ROJA)
            Card(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.CenterStart),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Leyenda", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.width(20.dp).height(4.dp).background(Color(0xFFFF0000), RoundedCornerShape(2.dp)))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Ciclovía", fontSize = 12.sp, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(16.dp).background(Color(0xFF0A6BEA), RoundedCornerShape(8.dp)))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Estacionamiento", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
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