package movil.proyecto_moviles.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

// 1. Modificamos label e icon para que acepten nulos y tengan un valor por defecto
sealed class AppDestination(
    val route: String,
    val label: String? = null,
    val icon: ImageVector? = null
) {
    // --- RUTAS DE AUTENTICACIÓN (No necesitan ícono ni texto en barra inferior) ---
    object Inicio : AppDestination("inicio")
    object Registro : AppDestination("registro")
    object Sesion : AppDestination("sesion")
    object Principal : AppDestination("principal") // El contenedor de tu mapa

    object NavegacionPreview : AppDestination("navegacion_preview")

    // --- RUTAS DEL BOTTOM NAVIGATION BAR (Sí necesitan ícono y texto) ---
    object Mapa : AppDestination("mapa", "Mapa", Icons.Filled.Place)
    object Buscar : AppDestination("buscar", "Buscar", Icons.Filled.Search)
    object Ruta : AppDestination("ruta", "Ruta", Icons.Filled.LocationOn)
    object Favoritos : AppDestination("favoritos", "Favoritos", Icons.Filled.Favorite)
    object Perfil : AppDestination("perfil", "Perfil", Icons.Filled.Person)

    companion object {
        // 2. Esta lista sigue funcionando igual para tu menú inferior
        val bottomItems = listOf(Mapa, Buscar, Ruta, Favoritos, Perfil)
    }
}