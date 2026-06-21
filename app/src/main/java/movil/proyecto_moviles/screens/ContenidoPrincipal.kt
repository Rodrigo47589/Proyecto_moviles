package movil.proyecto_moviles.screens

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import movil.proyecto_moviles.navigation.AppBottomBar
import movil.proyecto_moviles.navigation.AppNavHost

@Composable
fun ContenidoPrincipal() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            AppBottomBar(navController = navController)
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            innerPadding = innerPadding
        )
    }
}