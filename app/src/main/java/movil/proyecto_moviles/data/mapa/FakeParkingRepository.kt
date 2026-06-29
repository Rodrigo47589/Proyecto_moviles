package movil.proyecto_moviles.data.mapa

import movil.proyecto_moviles.screens.mapa.ParkingUi

class FakeParkingRepository : ParkingRepository {
    override suspend fun getNearbyParkings(
        userLat: Double,
        userLng: Double,
        radiusM: Int,
        searchText: String?
    ): List<ParkingUi> {
        return listOf(
            ParkingUi(
                id = "1",
                name = "Parque Kennedy",
                distanceMeters = 350,
                securityLevel = "Alta",
                bikeAvailable = 8,
                bikeCapacity = 12,
                scooterAvailable = 4,
                scooterCapacity = 8,
                latitude = -12.1211,
                longitude = -77.0297
            )
        )
    }
}