package movil.proyecto_moviles.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import movil.proyecto_moviles.screens.common.PlaceholderPantalla
import movil.proyecto_moviles.screens.mapa.MapaScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    innerPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Mapa.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(AppDestination.Mapa.route) {
            MapaScreen()
        }

        composable(AppDestination.Buscar.route) {
            PlaceholderPantalla("Buscar")
        }

        composable(AppDestination.Ruta.route) {
            PlaceholderPantalla("Ruta")
        }

        composable(AppDestination.Favoritos.route) {
            PlaceholderPantalla("Favoritos")
        }

        composable(AppDestination.Perfil.route) {
            PlaceholderPantalla("Perfil")
        }
    }
}