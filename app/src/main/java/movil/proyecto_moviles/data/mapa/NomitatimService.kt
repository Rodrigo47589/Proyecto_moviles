package movil.proyecto_moviles.data.mapa

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class NominatimService {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun buscarLugar(query: String): List<NominatimResult> {
        return try {
            client.get("https://nominatim.openstreetmap.org/search") {
                parameter("q", query)
                parameter("format", "json")
                parameter("limit", "5")
                // Nominatim exige un 'User-Agent' para saber quién pregunta
                header("User-Agent", "ProyectoMovilesDanielApp")
            }.body()
        } catch (e: Exception) {
            Log.e("NominatimService", "Error buscando lugar: ${e.message}", e)
            emptyList()
        }
    }
}