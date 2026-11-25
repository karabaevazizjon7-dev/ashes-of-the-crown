package com.ashes.crown

/** A bidirectional road between two canonical world locations. */
internal data class WorldRoad(
    val from: String,
    val to: String,
    val hours: Int,
    val dangerous: Boolean
)

/** A resolved route. The path includes both the origin and destination. */
internal data class TravelRoute(
    val path: List<String>,
    val totalHours: Int,
    val dangerousSegments: Int
) {
    val isDangerous: Boolean get() = dangerousSegments > 0
    val routeText: String get() = path.joinToString(" → ")
}

/**
 * Deterministic shortest-time route planner. Roads are bidirectional and
 * independent of Compose, preferences and the current UI map layout.
 */
internal object TravelManager {
    private val roads = listOf(
        WorldRoad("Королевский замок", "Зимний сад", 1, false),
        WorldRoad("Королевский замок", "Старый мост", 2, true),
        WorldRoad("Королевский замок", "Нижний город", 2, false),
        WorldRoad("Зимний сад", "Старый мост", 2, false),
        WorldRoad("Старый мост", "Северная застава", 3, true),
        WorldRoad("Старый мост", "Воронья деревня", 3, true),
        WorldRoad("Старый мост", "Лесной тракт", 2, true),
        WorldRoad("Нижний город", "Рыночная площадь", 1, false),
        WorldRoad("Нижний город", "Порт Веймар", 4, true),
        WorldRoad("Рыночная площадь", "Порт Веймар", 2, true),
        WorldRoad("Порт Веймар", "Старая часовня", 3, true),
        WorldRoad("Лесной тракт", "Воронья деревня", 2, true),
        WorldRoad("Воронья деревня", "Северная застава", 3, true),
        WorldRoad("Северная застава", "Крепость Вальден", 4, true),
        WorldRoad("Лесной тракт", "Крепость Вальден", 5, true)
    )

    fun roadCatalogue(): List<WorldRoad> = roads.toList()

    fun findRoute(originName: String, destinationName: String): TravelRoute? {
        val origin = WorldManager.canonicalName(originName)
        val destination = WorldManager.canonicalName(destinationName)
        val known = WorldManager.catalogue().map { it.name }.toSet()
        if (origin !in known || destination !in known || origin == destination) return null

        val distance = mutableMapOf(origin to 0)
        val dangerousCount = mutableMapOf(origin to 0)
        val previous = mutableMapOf<String, String>()
        val unvisited = known.toMutableSet()

        while (unvisited.isNotEmpty()) {
            val current = unvisited
                .filter { distance.containsKey(it) }
                .minWithOrNull(compareBy<String> { distance[it] ?: Int.MAX_VALUE }.thenBy { it })
                ?: break
            if (current == destination) break
            unvisited.remove(current)

            roads.asSequence()
                .filter { it.from == current || it.to == current }
                .forEach { road ->
                    val neighbor = if (road.from == current) road.to else road.from
                    if (neighbor !in unvisited) return@forEach
                    val candidate = (distance[current] ?: Int.MAX_VALUE) + road.hours
                    val old = distance[neighbor]
                    if (old == null || candidate < old) {
                        distance[neighbor] = candidate
                        dangerousCount[neighbor] = (dangerousCount[current] ?: 0) + if (road.dangerous) 1 else 0
                        previous[neighbor] = current
                    }
                }
        }

        val totalHours = distance[destination] ?: return null
        val reversedPath = mutableListOf(destination)
        var cursor = destination
        while (cursor != origin) {
            cursor = previous[cursor] ?: return null
            reversedPath += cursor
        }
        return TravelRoute(
            path = reversedPath.asReversed(),
            totalHours = totalHours,
            dangerousSegments = dangerousCount[destination] ?: 0
        )
    }
}
