package movil.proyecto_moviles.data.mapa

import io.github.jan.supabase.postgrest.from
import movil.proyecto_moviles.core.SupabaseProvider

class SupabaseBikeRouteRepository : BikeRouteRepository {

    override suspend fun getBikeRoutes() =
        SupabaseProvider.client
            .from("bike_routes")
            .select()
            .decodeList<BikeRouteDto>()
            .map { it.toUi() }
            .filter { it.points.size >= 2 }
}