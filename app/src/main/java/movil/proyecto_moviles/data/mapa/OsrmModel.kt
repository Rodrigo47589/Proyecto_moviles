package movil.proyecto_moviles.data.mapa

import kotlinx.serialization.Serializable

@Serializable
data class OsrmResponse(
    val code: String,
    val routes: List<OsrmRoute> = emptyList()
)

@Serializable
data class OsrmRoute(
    val distance: Double, // metros
    val duration: Double, // segundos
    val geometry: OsrmGeometry
)

@Serializable
data class OsrmGeometry(
    val coordinates: List<List<Double>>, // [ [lon, lat], [lon, lat], ... ]
    val type: String
)