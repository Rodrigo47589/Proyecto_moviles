package movil.proyecto_moviles.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Mapa : AppDestination("mapa", "Mapa", Icons.Filled.Place)
    object Buscar : AppDestination("buscar", "Buscar", Icons.Filled.Search)
    object Ruta : AppDestination("ruta", "Ruta", Icons.Filled.LocationOn)
    object Favoritos : AppDestination("favoritos", "Favoritos", Icons.Filled.Favorite)
    object Perfil : AppDestination("perfil", "Perfil", Icons.Filled.Person)

    companion object {
        val bottomItems = listOf(Mapa, Buscar, Ruta, Favoritos, Perfil)
    }
}