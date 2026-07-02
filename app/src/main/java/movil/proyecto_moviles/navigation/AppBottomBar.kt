package movil.proyecto_moviles.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppBottomBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White
    ) {
        AppDestination.bottomItems.forEach { destination ->
            val selected = currentRoute == destination.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != destination.route) {
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    // Agregamos !! porque estamos seguros de que estos destinos SÍ tienen ícono
                    Icon(
                        imageVector = destination.icon!!,
                        contentDescription = destination.label
                    )
                },
                label = {
                    // Usamos un valor por defecto en caso de que sea nulo (aunque sabemos que no lo será)
                    Text(text = destination.label ?: "")
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF156FE6),
                    selectedTextColor = Color(0xFF156FE6),
                    indicatorColor = Color(0xFFE5F0FF),
                    unselectedIconColor = Color(0xFF6B7280),
                    unselectedTextColor = Color(0xFF6B7280)
                )
            )
        }
    }
}