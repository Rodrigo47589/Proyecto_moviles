package movil.proyecto_moviles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import movil.proyecto_moviles.screens.PantallaBienvenida
import movil.proyecto_moviles.screens.RegistroScreen
import movil.proyecto_moviles.ui.theme.Proyecto_movilesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Proyecto_movilesTheme {
                AppMoviRed()
            }
        }
    }
}

@Composable
fun AppMoviRed() {
    var pantallaActual by rememberSaveable { mutableStateOf("inicio") }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        when (pantallaActual) {
            "inicio" -> PantallaBienvenida(
                modifier = Modifier.padding(innerPadding),
                onCrearCuentaClick = {
                    pantallaActual = "registro"
                }
            )

            "registro" -> RegistroScreen(
                modifier = Modifier.padding(innerPadding),
                onRegistroCompletado = {
                    pantallaActual = "inicio"
                }
            )
        }
    }
}