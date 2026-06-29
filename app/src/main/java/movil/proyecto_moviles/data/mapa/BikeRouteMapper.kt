package movil.proyecto_moviles.data.mapa

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import movil.proyecto_moviles.screens.mapa.BikeRoutePointUi
import movil.proyecto_moviles.screens.mapa.BikeRouteUi

fun BikeRouteDto.toUi(): BikeRouteUi {
    return BikeRouteUi(
        id = id,
        name = name,
        points = extractLineStringPoints(geometry)
    )
}

private fun extractLineStringPoints(geometry: JsonObject): List<BikeRoutePointUi> {
    val coordinates = geometry["coordinates"]?.jsonArray ?: return emptyList()

    return coordinates.mapNotNull { pointElement ->
        val pointArray = pointElement as? JsonArray ?: return@mapNotNull null
        if (pointArray.size < 2) return@mapNotNull null

        val lon = pointArray[0].jsonPrimitive.doubleOrNull ?: return@mapNotNull null
        val lat = pointArray[1].jsonPrimitive.doubleOrNull ?: return@mapNotNull null

        BikeRoutePointUi(
            latitude = lat,
            longitude = lon
        )
    }
}