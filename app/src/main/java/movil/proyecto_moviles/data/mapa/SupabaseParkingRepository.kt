package movil.proyecto_moviles.data.mapa

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import movil.proyecto_moviles.BuildConfig
import movil.proyecto_moviles.screens.mapa.ParkingUi

class SupabaseParkingRepository : ParkingRepository {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(json)
        }
    }

    override suspend fun getNearbyParkings(
        userLat: Double,
        userLng: Double,
        radiusM: Int,
        searchText: String?
    ): List<ParkingUi> {
        val response = client.post("${BuildConfig.SUPABASE_URL}/rest/v1/rpc/nearby_parkings") {
            header("apikey", BuildConfig.SUPABASE_KEY)
            header("Authorization", "Bearer ${BuildConfig.SUPABASE_KEY}")
            header(HttpHeaders.Accept, "application/json")
            contentType(ContentType.Application.Json)

            setBody(
                buildJsonObject {
                    put("user_lat", userLat)
                    put("user_lng", userLng)
                    put("radius_m", radiusM)

                    if (searchText.isNullOrBlank()) {
                        put("search_text", JsonNull)
                    } else {
                        put("search_text", searchText)
                    }
                }
            )
        }

        val raw = response.bodyAsText()

        Log.d("SupabaseParkingRepo", "status=${response.status.value}")
        Log.d("SupabaseParkingRepo", "raw=$raw")

        if (!response.status.isSuccess()) {
            throw IllegalStateException("RPC nearby_parkings falló: ${response.status.value} - $raw")
        }

        val decoded = json.decodeFromString<List<NearbyParkingDto>>(raw)

        Log.d("SupabaseParkingRepo", "decoded size=${decoded.size}")

        return decoded.map { it.toUi() }
    }
}