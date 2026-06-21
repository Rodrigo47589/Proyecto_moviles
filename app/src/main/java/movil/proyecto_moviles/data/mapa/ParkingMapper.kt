package movil.proyecto_moviles.data.mapa

import movil.proyecto_moviles.screens.mapa.ParkingUi

fun ParkingDto.toUi(): ParkingUi {
    return ParkingUi(
        id = id,
        name = name,
        distanceMeters = 0,
        securityLevel = securityLevel,
        bikeAvailable = bikeAvailable,
        bikeCapacity = bikeCapacity,
        scooterAvailable = scooterAvailable,
        scooterCapacity = scooterCapacity,
        latitude = latitude,
        longitude = longitude
    )
}