package movil.proyecto_moviles.navigation

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import movil.proyecto_moviles.screens.PantallaBienvenida
import movil.proyecto_moviles.screens.RegistroScreen
import movil.proyecto_moviles.screens.SesionPantalla
import movil.proyecto_moviles.screens.common.PlaceholderPantalla
import movil.proyecto_moviles.screens.mapa.MapaScreen
import movil.proyecto_moviles.screens.mapa.MapaViewModel

import movil.proyecto_moviles.screens.BuscarScreen
import movil.proyecto_moviles.screens.RutaScreen
import movil.proyecto_moviles.screens.NavegacionPreviewScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    innerPadding: PaddingValues = PaddingValues()
) {
    // 🔑 CLAVE: un solo MapaViewModel, con vida atada a la Activity,
    // compartido por Mapa, Ruta y NavegacionPreview.
    val activity = LocalContext.current as ComponentActivity
    val mapaViewModel: MapaViewModel = viewModel(viewModelStoreOwner = activity)

    NavHost(
        navController = navController,
        startDestination = AppDestination.Inicio.route,
        modifier = Modifier.padding(innerPadding)
    ) {

        // ==========================================
        // 1. FLUJO DE AUTENTICACIÓN
        // ==========================================
        composable(AppDestination.Inicio.route) {
            PantallaBienvenida(
                onCrearCuentaClick = { navController.navigate(AppDestination.Registro.route) },
                onYaTengoCuentaClick = { navController.navigate(AppDestination.Sesion.route) }
            )
        }

        composable(AppDestination.Registro.route) {
            RegistroScreen(
                onRegistroCompletado = {
                    navController.navigate(AppDestination.Mapa.route) {
                        popUpTo(AppDestination.Inicio.route) { inclusive = true }
                    }
                },
                onVolverInicio = { navController.popBackStack() }
            )
        }

        composable(AppDestination.Sesion.route) {
            SesionPantalla(
                onVolverClick = { navController.popBackStack() },
                onIniciarSesionClick = {
                    navController.navigate(AppDestination.Mapa.route) {
                        popUpTo(AppDestination.Inicio.route) { inclusive = true }
                    }
                },
                onOlvidoPasswordClick = { /* Lógica futura */ },
                onGoogleSignInClick = { /* Lógica futura */ }
            )
        }

        // ==========================================
        // 2. FLUJO PRINCIPAL
        // ==========================================
        composable(AppDestination.Mapa.route) {
            MapaScreen(viewModel = mapaViewModel)
        }

        composable(AppDestination.Buscar.route) {
            BuscarScreen()
        }

        composable(AppDestination.Ruta.route) {
            RutaScreen(
                viewModel = mapaViewModel,
                onDestinoSeleccionado = {
                    navController.navigate("navegacion_preview")
                }
            )
        }

        composable("navegacion_preview") {
            NavegacionPreviewScreen(viewModel = mapaViewModel)
        }

        composable(AppDestination.Favoritos.route) {
            PlaceholderPantalla("Favoritos")
        }

        composable(AppDestination.Perfil.route) {
            PlaceholderPantalla("Perfil")
        }
    }
}