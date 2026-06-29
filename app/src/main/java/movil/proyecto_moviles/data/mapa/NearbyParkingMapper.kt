package movil.proyecto_moviles.data.mapa

import movil.proyecto_moviles.screens.mapa.ParkingUi

fun NearbyParkingDto.toUi(): ParkingUi {
    return ParkingUi(
        id = id,
        name = name,
        distanceMeters = distanceMeters,
        securityLevel = securityLevel,
        bikeAvailable = bikeAvailable,
        bikeCapacity = bikeCapacity,
        scooterAvailable = scooterAvailable,
        scooterCapacity = scooterCapacity,
        latitude = latitude,
        longitude = longitude
    )
}