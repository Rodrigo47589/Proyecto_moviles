package movil.proyecto_moviles.data.mapa

import movil.proyecto_moviles.screens.mapa.ParkingUi

class FakeParkingRepository : ParkingRepository {
    override fun getNearbyParkings(): List<ParkingUi> {
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
            ),
            ParkingUi(
                id = "2",
                name = "CC Larcomar",
                distanceMeters = 500,
                securityLevel = "Alta",
                bikeAvailable = 15,
                bikeCapacity = 20,
                scooterAvailable = 8,
                scooterCapacity = 10,
                latitude = -12.1311,
                longitude = -77.0302
            ),
            ParkingUi(
                id = "3",
                name = "Est. 28 de Julio",
                distanceMeters = 750,
                securityLevel = "Media",
                bikeAvailable = 2,
                bikeCapacity = 10,
                scooterAvailable = 1,
                scooterCapacity = 5,
                latitude = -12.1292,
                longitude = -77.0248
            )
        )
    }
}