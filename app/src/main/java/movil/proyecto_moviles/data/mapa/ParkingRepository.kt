package movil.proyecto_moviles.data.mapa

import movil.proyecto_moviles.screens.mapa.ParkingUi

interface ParkingRepository {
    suspend fun getNearbyParkings(
        userLat: Double,
        userLng: Double,
        radiusM: Int,
        searchText: String? = null
    ): List<ParkingUi>
}