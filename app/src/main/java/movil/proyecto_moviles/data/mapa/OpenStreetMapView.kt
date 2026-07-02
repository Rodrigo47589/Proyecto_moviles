package movil.proyecto_moviles.data.mapa

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import movil.proyecto_moviles.screens.mapa.BikeRouteUi
import movil.proyecto_moviles.screens.mapa.ParkingUi
import movil.proyecto_moviles.screens.mapa.UserLocationUi
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@Composable
fun OpenStreetMapView(
    bikeRoutes: List<BikeRouteUi>,
    parkings: List<ParkingUi>,
    userLocation: UserLocationUi?,
    modifier: Modifier = Modifier
) {

    AndroidView(
        modifier = modifier,

        factory = { context ->

            Configuration.getInstance().userAgentValue =
                context.packageName

            MapView(context).apply {

                setTileSource(TileSourceFactory.MAPNIK)

                setMultiTouchControls(true)

                controller.setZoom(14.0)

                controller.setCenter(
                    GeoPoint(-12.0464, -77.0428)
                )
            }
        },

        update = { mapView ->

            mapView.overlays.clear()

            // =========================
            // CICLOVIAS
            // =========================

            bikeRoutes.forEach { route ->

                val polyline = Polyline()

                polyline.setPoints(
                    route.points.map {
                        GeoPoint(
                            it.latitude,
                            it.longitude
                        )
                    }
                )

                mapView.overlays.add(polyline)
            }

            // =========================
            // ESTACIONAMIENTOS
            // =========================

            parkings.forEach { parking ->

                val marker = Marker(mapView)

                marker.position = GeoPoint(
                    parking.latitude,
                    parking.longitude
                )

                marker.title = parking.name

                marker.subDescription =
                    "Disponibles: ${parking.totalFreeSpaces}"

                marker.setAnchor(
                    Marker.ANCHOR_CENTER,
                    Marker.ANCHOR_BOTTOM
                )

                mapView.overlays.add(marker)
            }

            // =========================
            // UBICACION USUARIO
            // =========================

            userLocation?.let { location ->

                val userMarker = Marker(mapView)

                userMarker.position = GeoPoint(
                    location.latitude,
                    location.longitude
                )

                userMarker.title = "Tu ubicación"

                userMarker.setAnchor(
                    Marker.ANCHOR_CENTER,
                    Marker.ANCHOR_CENTER
                )

                // Icono pequeño default de OSMDroid
                userMarker.icon = ContextCompat.getDrawable(
                    mapView.context,
                    org.osmdroid.library.R.drawable.person
                )

                mapView.overlays.add(userMarker)
            }

            mapView.invalidate()
        }
    )
}