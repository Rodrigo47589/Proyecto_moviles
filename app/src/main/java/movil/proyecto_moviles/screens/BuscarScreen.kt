package movil.proyecto_moviles.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscarScreen() {
    var textoBusqueda by remember { mutableStateOf("") }
    // Esta variable controla si mostramos o no los recientes
    var haBuscado by remember { mutableStateOf(false) }

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

        Text(
            text = "Buscar",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Barra de búsqueda
        TextField(
            value = textoBusqueda,
            onValueChange = { textoBusqueda = it },
            placeholder = { Text("Buscar estacionamiento o zona", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = fondoClaro,
                unfocusedContainerColor = fondoClaro,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (textoBusqueda.isNotBlank()) {
                        haBuscado = true // Activa la sección de recientes
                    }
                }
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Chips (Filtros)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ChipFiltro("Filtros", Icons.Outlined.List, fondoClaro)
            ChipFiltro("Bici", Icons.Outlined.Place, fondoClaro) // Reemplaza con ícono de bici real
            ChipFiltro("Scooter", Icons.Outlined.Face, fondoClaro) // Reemplaza con ícono de scooter
        }

        Spacer(modifier = Modifier.height(24.dp))

        // LÓGICA CONDICIONAL: Solo aparece si 'haBuscado' es true
        if (haBuscado) {
            Text(text = "Búsquedas recientes", fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(12.dp))

            ItemReciente("Parque Kennedy", "Miraflores", fondoClaro, azulPrincipal)
            ItemReciente("CC Jockey Plaza", "Santiago de Surco", fondoClaro, azulPrincipal)
            ItemReciente("Estación La Cultura", "San Borja", fondoClaro, azulPrincipal)

            Spacer(modifier = Modifier.height(24.dp))
        }

        Text(text = "Distritos populares", fontWeight = FontWeight.Bold, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(12.dp))

        // Grid de distritos
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TarjetaDistrito("Miraflores", fondoClaro, azulPrincipal, Modifier.weight(1f))
            TarjetaDistrito("San Isidro", fondoClaro, azulPrincipal, Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TarjetaDistrito("Barranco", fondoClaro, azulPrincipal, Modifier.weight(1f))
            TarjetaDistrito("Surco", fondoClaro, azulPrincipal, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// --- Componentes reutilizables para mantener el código limpio ---

@Composable
fun ChipFiltro(texto: String, icono: androidx.compose.ui.graphics.vector.ImageVector, fondo: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(fondo)
            .clickable { /* Acción del filtro */ }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = texto, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ItemReciente(titulo: String, subtitulo: String, fondo: Color, colorIcono: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(fondo)
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
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = colorIcono) // Usa un ícono de reloj
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = titulo, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = subtitulo, color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
fun TarjetaDistrito(nombre: String, fondo: Color, colorIcono: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(fondo)
            .clickable { /* Acción */ }
            .padding(16.dp)
    ) {
        Icon(Icons.Outlined.Place, contentDescription = null, tint = colorIcono)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = nombre, fontWeight = FontWeight.Bold)
    }
}