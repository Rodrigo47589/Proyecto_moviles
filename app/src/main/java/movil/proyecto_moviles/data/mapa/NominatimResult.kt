package movil.proyecto_moviles.data.mapa

import kotlinx.serialization.Serializable

@Serializable
data class NominatimResult(
    val lat: String,
    val lon: String,
    val display_name: String
)