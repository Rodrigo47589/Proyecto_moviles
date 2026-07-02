package movil.proyecto_moviles.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import movil.proyecto_moviles.data.mapa.NominatimResult
import movil.proyecto_moviles.screens.mapa.MapaEvent
import movil.proyecto_moviles.screens.mapa.MapaViewModel
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

// Modelo simple para los destinos "hardcodeados" pero con coordenadas reales
private data class DestinoSugerido(
    val titulo: String,
    val subtitulo: String,
    val lat: Double,
    val lon: Double
)

private val destinosSugeridos = listOf(
    DestinoSugerido("Parque Kennedy", "Miraflores", -12.1215, -77.0296),
    DestinoSugerido("CC Larcomar", "Miraflores", -12.1319, -77.0303),
    DestinoSugerido("Malecón de Miraflores", "Miraflores", -12.1300, -77.0280)
)

@Composable
fun RutaScreen(
    viewModel: MapaViewModel,
    onDestinoSeleccionado: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    val azulPrincipal = Color(0xFF156FE6)
    val fondoClaro = Color(0xFFF0F4F9)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text("Planifica tu ruta", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(fondoClaro)
                .padding(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 28.dp)
                ) {
                    Box(modifier = Modifier.size(12.dp).background(Color(0xFF00C853), CircleShape))
                    Box(modifier = Modifier.width(2.dp).height(50.dp).background(Color.LightGray))
                    Box(modifier = Modifier.size(12.dp).background(azulPrincipal, CircleShape))
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text("Origen", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(horizontal = 12.dp, vertical = 12.dp)
                    ) {
                        Text("Mi ubicación", fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Destino", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(horizontal = 12.dp, vertical = 12.dp)
                    ) {
                        // Campo de texto conectado al ViewModel
                        BasicTextField(
                            value = uiState.destinationQuery,
                            onValueChange = {
                                viewModel.onEvent(MapaEvent.OnDestinationQueryChanged(it))
                            },
                            textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = { /* Búsqueda automática con debounce */ }
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { innerTextField ->
                                if (uiState.destinationQuery.isEmpty()) {
                                    Text("¿A dónde vas?", color = Color.Gray, fontSize = 14.sp)
                                }
                                innerTextField()
                            }
                        )
                    }

                    // Sugerencias reales de Nominatim mientras el usuario escribe
                    if (uiState.destinationSuggestions.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                        ) {
                            uiState.destinationSuggestions.forEach { sugerencia ->
                                Text(
                                    text = sugerencia.display_name,
                                    fontSize = 13.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.onEvent(MapaEvent.OnSuggestionSelected(sugerencia))
                                            onDestinoSeleccionado()
                                        }
                                        .padding(horizontal = 12.dp, vertical = 10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Destinos sugeridos", fontWeight = FontWeight.Bold, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(12.dp))

        // DIBUJANDO LAS SUGERENCIAS CON CÁLCULO LOCAL
        destinosSugeridos.forEach { destino ->
            var tiempoStr = "-- min"
            var distanciaStr = "-- km"

            // Si ya tenemos la ubicación del usuario, calculamos el estimado al instante
            uiState.userLocation?.let { origen ->
                val estimado = estimarTiempoYDistancia(
                    origenLat = origen.latitude,
                    origenLon = origen.longitude,
                    destLat = destino.lat,
                    destLon = destino.lon
                )
                tiempoStr = estimado.first
                distanciaStr = estimado.second
            }

            ItemSugerido(
                titulo = destino.titulo,
                subtitulo = destino.subtitulo,
                tiempo = tiempoStr,
                distancia = distanciaStr,
                fondo = fondoClaro,
                azul = azulPrincipal,
                onClick = {
                    viewModel.onEvent(
                        MapaEvent.OnSuggestionSelected(
                            NominatimResult(
                                lat = destino.lat.toString(),
                                lon = destino.lon.toString(),
                                display_name = "${destino.titulo}, ${destino.subtitulo}"
                            )
                        )
                    )
                    onDestinoSeleccionado()
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, Color(0xFFB3D4FF), RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(fondoClaro),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = azulPrincipal)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "Tip de Seguridad", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Las ciclovías de Lima son compartidas para bicicletas y scooters. Te mostraremos las rutas más seguras.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ItemSugerido(
    titulo: String,
    subtitulo: String,
    tiempo: String,
    distancia: String,
    fondo: Color,
    azul: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(fondo)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = azul)
        }
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = titulo, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = subtitulo, color = Color.Gray, fontSize = 12.sp)
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(text = tiempo, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = azul)
            Text(text = distancia, color = Color.Gray, fontSize = 10.sp)
        }

        Spacer(modifier = Modifier.width(8.dp))
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
    }
}

// =====================================================================
// FUNCIÓN MATEMÁTICA: Aproximación urbana para vistas previas rápidas
// =====================================================================
private fun estimarTiempoYDistancia(
    origenLat: Double, origenLon: Double,
    destLat: Double, destLon: Double
): Pair<String, String> {

    // 1. Distancia en línea recta (Fórmula de Haversine)
    val r = 6371.0 // Radio de la tierra en km
    val dLat = Math.toRadians(destLat - origenLat)
    val dLon = Math.toRadians(destLon - origenLon)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(origenLat)) * cos(Math.toRadians(destLat)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    val distanciaLineaRectaKm = r * c

    // 2. Factor Urbano: multiplicamos por 1.35 porque las calles de Lima
    // no son líneas rectas perfectas (hacen "L" o zigzag).
    val distanciaEstimadaKm = distanciaLineaRectaKm * 1.35

    // 3. Tiempo estimado: asumiendo una velocidad en bici de 15 km/h
    val tiempoHoras = distanciaEstimadaKm / 15.0
    val tiempoMinutos = (tiempoHoras * 60).roundToInt()

    return Pair("${tiempoMinutos} min", "%.1f km".format(distanciaEstimadaKm))
}