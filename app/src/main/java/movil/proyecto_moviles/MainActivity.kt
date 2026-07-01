package movil.proyecto_moviles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import movil.proyecto_moviles.screens.ContenidoPrincipal
import movil.proyecto_moviles.ui.theme.Proyecto_movilesTheme

//Class
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Proyecto_movilesTheme {
                ContenidoPrincipal()
            }
        }
    }
}
