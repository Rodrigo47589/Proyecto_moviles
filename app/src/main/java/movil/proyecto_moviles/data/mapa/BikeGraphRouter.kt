package movil.proyecto_moviles.data.mapa

import movil.proyecto_moviles.screens.mapa.BikeRoutePointUi
import movil.proyecto_moviles.screens.mapa.BikeRouteUi
import java.util.PriorityQueue
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class BikeGraphRouter(
    bikeRoutes: List<BikeRouteUi>,
    private val snapToleranceMeters: Double = 15.0 // Mantenlo bajo para no deformar las calles
) {

    data class Node(val id: Int, val lat: Double, val lon: Double)

    private val nodes = mutableListOf<Node>()
    private val adjacency = HashMap<Int, MutableList<Pair<Int, Double>>>()

    init {
        // Solo construimos las calles reales, respetando su forma exacta
        bikeRoutes.forEach { route ->
            var prevId: Int? = null
            route.points.forEach { point ->
                val nodeId = getOrCreateNode(point.latitude, point.longitude)
                if (prevId != null && prevId != nodeId) {
                    val dist = distanciaMetros(
                        nodes[prevId!!].lat, nodes[prevId!!].lon,
                        nodes[nodeId].lat, nodes[nodeId].lon
                    )
                    addEdge(prevId!!, nodeId, dist)
                }
                prevId = nodeId
            }
        }
    }

    private fun getOrCreateNode(lat: Double, lon: Double): Int {
        val existente = nodes.firstOrNull {
            distanciaMetros(it.lat, it.lon, lat, lon) <= snapToleranceMeters
        }
        if (existente != null) return existente.id

        val nuevoId = nodes.size
        nodes.add(Node(nuevoId, lat, lon))
        return nuevoId
    }

    private fun addEdge(a: Int, b: Int, weight: Double) {
        if (adjacency[a]?.any { it.first == b } == true) return
        adjacency.getOrPut(a) { mutableListOf() }.add(b to weight)
        adjacency.getOrPut(b) { mutableListOf() }.add(a to weight)
    }

    fun nearestNode(point: BikeRoutePointUi): Node? =
        nodes.minByOrNull { distanciaMetros(it.lat, it.lon, point.latitude, point.longitude) }

    fun distanceToNode(point: BikeRoutePointUi, node: Node): Double =
        distanciaMetros(point.latitude, point.longitude, node.lat, node.lon)

    fun pathDistanceMeters(path: List<BikeRoutePointUi>): Double {
        if (path.size < 2) return 0.0
        return path.zipWithNext().sumOf { (a, b) ->
            distanciaMetros(a.latitude, a.longitude, b.latitude, b.longitude)
        }
    }

    // ==============================================================
    // NUEVO ALGORITMO: Búsqueda del mejor punto de abordaje
    // ==============================================================
    fun findBestPath(origin: BikeRoutePointUi, destination: BikeRoutePointUi): Pair<List<BikeRoutePointUi>, Node>? {
        // 1. Encontramos la ciclovía en el destino
        val nodoSalida = nearestNode(destination) ?: return null

        // 2. Mapeamos toda la red conectada a ese destino
        val dist = HashMap<Int, Double>()
        val prev = HashMap<Int, Int>()
        val queue = PriorityQueue<Pair<Int, Double>>(compareBy { it.second })

        dist[nodoSalida.id] = 0.0
        queue.add(nodoSalida.id to 0.0)

        while (queue.isNotEmpty()) {
            val (actual, d) = queue.poll()
            if (d > (dist[actual] ?: Double.MAX_VALUE)) continue

            adjacency[actual]?.forEach { (vecino, peso) ->
                val nuevaDist = d + peso
                if (nuevaDist < (dist[vecino] ?: Double.MAX_VALUE)) {
                    dist[vecino] = nuevaDist
                    prev[vecino] = actual
                    queue.add(vecino to nuevaDist)
                }
            }
        }

        if (dist.isEmpty()) return null

        // 3. De toda esa red conectada, buscamos la "puerta de entrada" más cercana al usuario
        var bestNodeId = -1
        var minOriginDist = Double.MAX_VALUE

        dist.keys.forEach { nodeId ->
            val node = nodes[nodeId]
            val dToOrigin = distanciaMetros(origin.latitude, origin.longitude, node.lat, node.lon)
            if (dToOrigin < minOriginDist) {
                minOriginDist = dToOrigin
                bestNodeId = nodeId
            }
        }

        if (bestNodeId == -1) return null

        // 4. Reconstruimos el camino desde la puerta de entrada hasta el destino
        val camino = mutableListOf<Int>()
        var actual = bestNodeId
        camino.add(actual)
        while (actual != nodoSalida.id) {
            actual = prev[actual] ?: return null
            camino.add(actual)
        }

        val pathPoints = camino.map { id -> nodes[id].let { BikeRoutePointUi(it.lat, it.lon) } }
        return Pair(pathPoints, nodes[bestNodeId])
    }

    companion object {
        fun distanciaMetros(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val r = 6371000.0
            val dLat = Math.toRadians(lat2 - lat1)
            val dLon = Math.toRadians(lon2 - lon1)
            val a = sin(dLat / 2) * sin(dLat / 2) +
                    cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                    sin(dLon / 2) * sin(dLon / 2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            return r * c
        }
    }
}