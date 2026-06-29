package movil.proyecto_moviles.data.mapa

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NearbyParkingDto(
    val id: String,
    val name: String,
    val address: String? = null,
    val district: String? = null,
    @SerialName("security_level")
    val securityLevel: String,
    @SerialName("bike_capacity")
    val bikeCapacity: Int,
    @SerialName("scooter_capacity")
    val scooterCapacity: Int,
    val latitude: Double,
    val longitude: Double,
    @SerialName("bike_available")
    val bikeAvailable: Int,
    @SerialName("scooter_available")
    val scooterAvailable: Int,
    @SerialName("distance_meters")
    val distanceMeters: Int
)