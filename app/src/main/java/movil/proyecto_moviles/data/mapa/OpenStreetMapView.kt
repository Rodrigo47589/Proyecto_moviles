package movil.proyecto_moviles.data.mapa

import android.graphics.Color as AndroidColor
import android.graphics.DashPathEffect
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import movil.proyecto_moviles.screens.mapa.BikeRoutePointUi
import movil.proyecto_moviles.screens.mapa.BikeRouteUi
import movil.proyecto_moviles.screens.mapa.ParkingUi
import movil.proyecto_moviles.screens.mapa.RouteSegmentType
import movil.proyecto_moviles.screens.mapa.RouteSegmentUi
import movil.proyecto_moviles.screens.mapa.UserLocationUi
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private fun distanciaMetros(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371000.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return r * c
}

private fun distanciaPuntoASegmento(
    px: Double, py: Double,
    ax: Double, ay: Double,
    bx: Double, by: Double
): Double {
    val dx = bx - ax
    val dy = by - ay
    if (dx == 0.0 && dy == 0.0) return distanciaMetros(px, py, ax, ay)

    val t = (((px - ax) * dx + (py - ay) * dy) / (dx * dx + dy * dy))
        .coerceIn(0.0, 1.0)
    val projX = ax + t * dx
    val projY = ay + t * dy
    return distanciaMetros(px, py, projX, projY)
}

private fun rutaEstaEnCamino(
    route: BikeRouteUi,
    routePoints: List<BikeRoutePointUi>,
    umbralMetros: Double = 20.0
): Boolean {
    if (routePoints.isEmpty()) return true
    if (routePoints.size < 2) {
        val p0 = routePoints[0]
        return route.points.any { p ->
            distanciaMetros(p.latitude, p.longitude, p0.latitude, p0.longitude) <= umbralMetros
        }
    }

    return route.points.any { p ->
        routePoints.zipWithNext().any { (a, b) ->
            distanciaPuntoASegmento(
                p.latitude, p.longitude,
                a.latitude, a.longitude,
                b.latitude, b.longitude
            ) <= umbralMetros
        }
    }
}

@Composable
fun OpenStreetMapView(
    bikeRoutes: List<BikeRouteUi>,
    parkings: List<ParkingUi>,
    userLocation: UserLocationUi?,
    destinationLocation: UserLocationUi? = null,
    destinationName: String? = null,
    routeSegments: List<RouteSegmentUi> = emptyList(),
    // ¡NUEVO!: Callback para avisar cuando el mapa se mueve
    onMapCenterChange: ((Double, Double) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            Configuration.getInstance().userAgentValue = context.packageName

            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(14.0)
                controller.setCenter(GeoPoint(-12.0464, -77.0428))

                // Agregamos un oyente para detectar movimientos
                addMapListener(object : MapListener {
                    override fun onScroll(event: ScrollEvent?): Boolean {
                        onMapCenterChange?.invoke(mapCenter.latitude, mapCenter.longitude)
                        return false
                    }
                    override fun onZoom(event: ZoomEvent?): Boolean {
                        onMapCenterChange?.invoke(mapCenter.latitude, mapCenter.longitude)
                        return false
                    }
                })
            }
        },
        update = { mapView ->
            mapView.overlays.clear()

            val puntosDeRutaActive = routeSegments.flatMap { it.points }

            bikeRoutes.forEach { route ->
                if (puntosDeRutaActive.isEmpty() || rutaEstaEnCamino(route, puntosDeRutaActive, umbralMetros = 500.0)) {
                    val polyline = Polyline()
                    polyline.setPoints(route.points.map { GeoPoint(it.latitude, it.longitude) })
                    polyline.outlinePaint.color = AndroidColor.parseColor("#FF0000") // Rojo intenso
                    polyline.outlinePaint.strokeWidth = 8f
                    mapView.overlays.add(polyline)
                }
            }

            routeSegments.forEach { segment ->
                val polyline = Polyline()
                polyline.outlinePaint.isAntiAlias = true
                mapView.setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null)

                polyline.setPoints(segment.points.map { GeoPoint(it.latitude, it.longitude) })
                polyline.outlinePaint.color = AndroidColor.parseColor("#0A6BEA")

                when (segment.type) {
                    RouteSegmentType.BIKE_LANE -> {
                        polyline.outlinePaint.strokeWidth = 11f
                    }
                    RouteSegmentType.STREET -> {
                        polyline.outlinePaint.strokeWidth = 8f
                        polyline.outlinePaint.pathEffect = DashPathEffect(floatArrayOf(20f, 15f), 0f)
                    }
                }
                mapView.overlays.add(polyline)
            }

            parkings.forEach { parking ->
                val marker = Marker(mapView)
                marker.position = GeoPoint(parking.latitude, parking.longitude)
                marker.title = parking.name
                marker.subDescription = "Disponibles: ${parking.totalFreeSpaces}"
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                mapView.overlays.add(marker)
            }

            userLocation?.let { location ->
                val userMarker = Marker(mapView)
                userMarker.position = GeoPoint(location.latitude, location.longitude)
                userMarker.title = "Tu ubicación"
                userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                userMarker.icon = ContextCompat.getDrawable(
                    mapView.context,
                    org.osmdroid.library.R.drawable.person
                )
                mapView.overlays.add(userMarker)
            }

            destinationLocation?.let { destino ->
                val destinoMarker = Marker(mapView)
                destinoMarker.position = GeoPoint(destino.latitude, destino.longitude)
                destinoMarker.title = destinationName ?: "Destino"
                destinoMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                mapView.overlays.add(destinoMarker)
                destinoMarker.showInfoWindow()
            }

            mapView.post {
                try {
                    when {
                        userLocation != null && destinationLocation != null -> {
                            val distLat = abs(userLocation.latitude - destinationLocation.latitude)
                            val distLon = abs(userLocation.longitude - destinationLocation.longitude)

                            if (mapView.width > 0 && mapView.height > 0 && (distLat >= 0.0005 || distLon >= 0.0005)) {
                                val boundingBox = BoundingBox.fromGeoPoints(
                                    listOf(
                                        GeoPoint(userLocation.latitude, userLocation.longitude),
                                        GeoPoint(destinationLocation.latitude, destinationLocation.longitude)
                                    )
                                )
                                mapView.zoomToBoundingBox(boundingBox, false, 100)
                            }
                        }
                        destinationLocation != null -> {
                            mapView.controller.setCenter(
                                GeoPoint(destinationLocation.latitude, destinationLocation.longitude)
                            )
                            mapView.controller.setZoom(16.0)
                        }
                        // Quitamos el auto-centrado del usuario de aquí para que no pelee contigo cuando intentes arrastrar el mapa
                    }
                } catch (e: Exception) {
                    android.util.Log.e("OpenStreetMapView", "Error ajustando cámara: ${e.message}", e)
                }
            }

            mapView.invalidate()
        }
    )
}