package movil.proyecto_moviles.screens.mapa

enum class RouteSegmentType { STREET, BIKE_LANE }

data class RouteSegmentUi(
    val points: List<BikeRoutePointUi>,
    val type: RouteSegmentType
)