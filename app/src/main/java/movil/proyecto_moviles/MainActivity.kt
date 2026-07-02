package movil.proyecto_moviles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

// Asegúrate de importar tus archivos de navegación
import movil.proyecto_moviles.navigation.AppBottomBar
import movil.proyecto_moviles.navigation.AppDestination
import movil.proyecto_moviles.navigation.AppNavHost
import movil.proyecto_moviles.ui.theme.Proyecto_movilesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Proyecto_movilesTheme {
                MoviRedApp()
            }
        }
    }
}

@Composable
fun MoviRedApp() {
    // 1. Creamos el controlador principal de toda la app
    val navController = rememberNavController()

    // 2. Leemos en qué pantalla estamos actualmente
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 3. Creamos una lista solo con las rutas que deben mostrar la barra inferior
    val rutasConBottomBar = AppDestination.bottomItems.map { it.route }

    // 4. Verificamos si la ruta actual está en esa lista
    val mostrarBottomBar = currentRoute in rutasConBottomBar

    Scaffold(
        bottomBar = {
            // 5. ¡Magia! Solo dibujamos la barra si estamos en las pantallas principales
            if (mostrarBottomBar) {
                AppBottomBar(navController = navController)
            }
        }
    ) { innerPadding ->
        // 6. El NavHost se encarga de mostrar la pantalla que corresponde
        // Le pasamos el innerPadding para que el contenido no quede oculto detrás de la barra
        AppNavHost(
            navController = navController,
            innerPadding = innerPadding
        )
    }
}