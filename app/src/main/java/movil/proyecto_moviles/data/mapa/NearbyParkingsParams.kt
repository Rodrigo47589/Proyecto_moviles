package movil.proyecto_moviles.data.mapa

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NearbyParkingsParams(
    @SerialName("user_lat")
    val userLat: Double,
    @SerialName("user_lng")
    val userLng: Double,
    @SerialName("radius_m")
    val radiusM: Int = 1500,
    @SerialName("search_text")
    val searchText: String? = null
)