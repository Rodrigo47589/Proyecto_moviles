package movil.proyecto_moviles.data.mapa

import movil.proyecto_moviles.screens.mapa.BikeRouteUi

interface BikeRouteRepository {
    suspend fun getBikeRoutes(): List<BikeRouteUi>
}