package movil.proyecto_moviles.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import movil.proyecto_moviles.data.mapa.OpenStreetMapView
import movil.proyecto_moviles.screens.mapa.MapaEvent
import movil.proyecto_moviles.screens.mapa.MapaViewModel
import kotlin.math.roundToInt

@Composable
fun NavegacionPreviewScreen(
    viewModel: MapaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val azulPrincipal = Color(0xFF156FE6)
    val verdeExito = Color(0xFF00C853)
    val fondoClaro = Color(0xFFF0F4F9)

    // ==========================================
    // EFECTO AUTOMÁTICO: Calcular ruta al tener destino
    // ==========================================
    LaunchedEffect(uiState.destinationLocation) {
        if (uiState.destinationLocation != null && uiState.routeSegments.isEmpty()) {
            viewModel.onEvent(MapaEvent.CalcularRutaEvent)
        }
    }

    // Textos formateados de distancia/tiempo (con fallback mientras no hay ruta)
    val tiempoTexto = uiState.routeDurationSeconds?.let {
        "${(it / 60).roundToInt()} min"
    } ?: "--"

    val distanciaTexto = uiState.routeDistanceMeters?.let {
        "%.1f km".format(it / 1000)
    } ?: "--"

    Box(modifier = Modifier.fillMaxSize()) {

        // ==========================================
        // CAPA 0: MAPA REAL + RUTA + DESTINO
        // ==========================================
        OpenStreetMapView(
            bikeRoutes = uiState.bikeRoutes,
            parkings = uiState.nearbyParkings,
            userLocation = uiState.userLocation,
            destinationLocation = uiState.destinationLocation,
            destinationName = uiState.destinationName, // ¡Nombre real para el marcador!
            routeSegments = uiState.routeSegments,     // ¡CORRECCIÓN APLICADA AQUÍ!
            modifier = Modifier.fillMaxSize()
        )

        // ==========================================
        // CAPA SUPERIOR: ORIGEN Y DESTINO (con autocompletado)
        // ==========================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 16.dp, end = 16.dp)
                .align(Alignment.TopCenter)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                    ) {
                        Box(modifier = Modifier.size(10.dp).background(verdeExito, CircleShape))
                        Box(modifier = Modifier.width(2.dp).height(30.dp).background(Color.LightGray))
                        Box(modifier = Modifier.size(10.dp).background(azulPrincipal, CircleShape))
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Origen", fontSize = 12.sp, color = Color.Gray)
                        Text("Mi ubicación", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Destino", fontSize = 12.sp, color = Color.Gray)

                        // Campo editable con autocompletado
                        BasicTextField(
                            value = uiState.destinationQuery,
                            onValueChange = {
                                viewModel.onEvent(MapaEvent.OnDestinationQueryChanged(it))
                            },
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            ),
                            singleLine = true,
                            decorationBox = { innerTextField ->
                                if (uiState.destinationQuery.isEmpty()) {
                                    Text(
                                        "Buscar destino (ej. Larcomar)",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(fondoClaro),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Autorenew, contentDescription = "Invertir", tint = Color.Gray)
                    }
                }
            }

            // Dropdown de sugerencias
            if (uiState.destinationSuggestions.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column {
                        uiState.destinationSuggestions.forEach { sugerencia ->
                            Text(
                                text = sugerencia.display_name,
                                fontSize = 13.sp,
                                color = Color.Black,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        viewModel.onEvent(MapaEvent.OnSuggestionSelected(sugerencia))
                                    }
                                    .background(Color.White)
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // CAPA INFERIOR: DETALLES DE LA RUTA Y BOTÓN
        // ==========================================
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Tiempo estimado", fontSize = 12.sp, color = Color.Gray)
                        Text(
                            text = if (uiState.loadingRoute) "..." else tiempoTexto,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Distancia", fontSize = 12.sp, color = Color.Gray)
                        Text(
                            text = if (uiState.loadingRoute) "..." else distanciaTexto,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoResumenCard(
                        icon = Icons.Default.DirectionsBike,
                        iconTint = azulPrincipal,
                        titulo = "Ciclovía",
                        valor = "85%", // Dinámico a futuro si lo deseas
                        modifier = Modifier.weight(1f)
                    )
                    InfoResumenCard(
                        icon = Icons.Default.Security,
                        iconTint = verdeExito,
                        titulo = "Seguridad",
                        valor = "Alta",
                        modifier = Modifier.weight(1f)
                    )
                    InfoResumenCard(
                        icon = Icons.Default.LocalParking,
                        iconTint = verdeExito,
                        titulo = "Parqueo",
                        valor = "8 libres",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { /* Lógica para arrancar el GPS paso a paso */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = azulPrincipal),
                    enabled = uiState.destinationLocation != null
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Iniciar navegación",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun InfoResumenCard(
    icon: ImageVector,
    iconTint: Color,
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF0F4F9))
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = titulo, fontSize = 12.sp, color = Color.Gray)
            Text(text = valor, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}