package movil.proyecto_moviles.data.mapa

import movil.proyecto_moviles.screens.mapa.ParkingUi

interface ParkingRepository {
    fun getNearbyParkings(): List<ParkingUi>
}