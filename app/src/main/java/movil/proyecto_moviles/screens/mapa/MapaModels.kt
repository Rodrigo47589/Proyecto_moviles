package movil.proyecto_moviles.screens.mapa

data class UserLocationUi(
    val latitude: Double,
    val longitude: Double
)

data class ParkingUi(
    val id: String,
    val name: String,
    val distanceMeters: Int,
    val securityLevel: String,
    val bikeAvailable: Int,
    val bikeCapacity: Int,
    val scooterAvailable: Int,
    val scooterCapacity: Int,
    val latitude: Double,
    val longitude: Double
) {
    val totalFreeSpaces: Int
        get() = bikeAvailable + scooterAvailable

    val availabilityLabel: String
        get() = when {
            totalFreeSpaces >= 15 -> "Alta"
            totalFreeSpaces >= 6 -> "Media"
            else -> "Baja"
        }
}

data class BikeRoutePointUi(
    val latitude: Double,
    val longitude: Double
)

data class BikeRouteUi(
    val id: String,
    val name: String?,
    val points: List<BikeRoutePointUi>
)