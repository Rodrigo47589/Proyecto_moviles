package movil.proyecto_moviles.data.mapa

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class BikeRouteDto(
    val id: String,
    val name: String? = null,
    val highway: String? = null,
    val cycleway: String? = null,
    val bicycle: String? = null,
    val segregated: String? = null,
    val surface: String? = null,
    val oneway: String? = null,
    val lit: String? = null,
    val width: String? = null,
    val description: String? = null,
    @SerialName("geom_type")
    val geomType: String,
    val geometry: JsonObject
)