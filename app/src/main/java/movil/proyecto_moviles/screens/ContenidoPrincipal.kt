package movil.proyecto_moviles.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ContenidoPrincipalScreen(onCerrarSesion: () -> Unit) {
    var pestañaActual by rememberSaveable { mutableStateOf("mapa") }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = pestañaActual == "mapa",
                    onClick = { pestañaActual = "mapa" },
                    label = { Text("Mapa") },
                    icon = { Icon(Icons.Default.LocationOn, contentDescription = "Mapa") }
                )
                NavigationBarItem(
                    selected = pestañaActual == "buscar",
                    onClick = { pestañaActual = "buscar" },
                    label = { Text("Buscar") },
                    icon = { Icon(Icons.Default.Search, contentDescription = "Buscar") }
                )
                NavigationBarItem(
                    selected = pestañaActual == "rutas",
                    onClick = { pestañaActual = "rutas" },
                    label = { Text("Rutas") },
                    icon = { Icon(Icons.Default.Place, contentDescription = "Rutas") }
                )
                NavigationBarItem(
                    selected = pestañaActual == "favoritos",
                    onClick = { pestañaActual = "favoritos" },
                    label = { Text("Favoritos") },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favoritos") }
                )
                NavigationBarItem(
                    selected = pestañaActual == "perfil",
                    onClick = { pestañaActual = "perfil" },
                    label = { Text("Perfil") },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (pestañaActual) {
                "mapa" -> PantallaMapa()
                "buscar" -> PantallaBuscar()
                "rutas" -> PantallaRutas()
                "favoritos" -> PantallaFavoritos()
                "perfil" -> PantallaPerfil(onCerrarSesion = onCerrarSesion)
            }
        }
    }
}

@Composable
fun PantallaMapa() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Mapa de Lima", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PantallaBuscar() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Buscador de Sitios", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PantallaRutas() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Rutas de Ciclismo/Tránsito", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PantallaFavoritos() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Lugares Guardados", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PantallaPerfil(onCerrarSesion: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Perfil de Usuario", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedButton(
            onClick = onCerrarSesion,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, Color(0xFFED3D3D)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFED3D3D))
        ) {
            Text(text = "Cerrar Sesión", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}