package movil.proyecto_moviles.data.mapa

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class OsrmService {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    /**
     * Perfil "cycling" porque tu app es de bicicletas.
     * Nota: usamos el servidor público de demo de OSRM.
     * Para producción con tráfico real, considera levantar tu propio servidor OSRM.
     */
    suspend fun obtenerRuta(
        origenLat: Double,
        origenLon: Double,
        destinoLat: Double,
        destinoLon: Double
    ): OsrmRoute? {
        return try {
            val response: OsrmResponse = client.get(
                "https://router.project-osrm.org/route/v1/cycling/$origenLon,$origenLat;$destinoLon,$destinoLat"
            ) {
                parameter("overview", "full")
                parameter("geometries", "geojson")
            }.body()

            response.routes.firstOrNull()
        } catch (e: Exception) {
            Log.e("OsrmService", "Error calculando ruta: ${e.message}", e)
            null
        }
    }
}