package com.ashes.crown

fun main() {
    check(TravelManager.roadCatalogue().size == 15)
    check(TravelManager.findRoute("Королевский замок", "Королевский замок") == null)
    check(TravelManager.findRoute("Неизвестное место", "Зимний сад") == null)
    check(TravelManager.findRoute("Королевский замок", "Неизвестное место") == null)

    val garden = TravelManager.findRoute("Королевский замок", "Зимний сад")!!
    check(garden.path == listOf("Королевский замок", "Зимний сад"))
    check(garden.totalHours == 1 && garden.dangerousSegments == 0 && !garden.isDangerous)

    val port = TravelManager.findRoute("Королевский замок", "Порт Эстервала")!!
    check(port.path.first() == "Королевский замок" && port.path.last() == "Порт Веймар")
    check(port.totalHours == 5)
    check(port.dangerousSegments == 1 && port.isDangerous)

    val chapel = TravelManager.findRoute("Королевский замок", "Старая часовня")!!
    check(chapel.path.first() == "Королевский замок" && chapel.path.last() == "Старая часовня")
    check(chapel.totalHours == 8 && chapel.isDangerous)

    val fortress = TravelManager.findRoute("Крепость Вальден", "Королевский замок")!!
    check(fortress.path.first() == "Крепость Вальден")
    check(fortress.path.last() == "Королевский замок")
    check(fortress.totalHours == 9)

    // Every catalog location is reachable from the capital; every route is reversible.
    WorldManager.catalogue().filter { it.name != "Королевский замок" }.forEach { place ->
        val outbound = TravelManager.findRoute("Королевский замок", place.name) ?: error("No route to ${place.name}")
        val inbound = TravelManager.findRoute(place.name, "Королевский замок") ?: error("No return route from ${place.name}")
        check(outbound.totalHours == inbound.totalHours)
        check(outbound.path.first() == "Королевский замок" && outbound.path.last() == place.name)
        check(inbound.path.first() == place.name && inbound.path.last() == "Королевский замок")
    }

    // Compare every ordered pair against an independent Floyd-Warshall reference.
    val names = WorldManager.catalogue().map { it.name }
    val infinity = 1_000_000
    val expected = Array(names.size) { row -> IntArray(names.size) { col -> if (row == col) 0 else infinity } }
    val roads = TravelManager.roadCatalogue()
    roads.forEach { road ->
        val from = names.indexOf(road.from)
        val to = names.indexOf(road.to)
        expected[from][to] = road.hours
        expected[to][from] = road.hours
    }
    for (middle in names.indices) for (from in names.indices) for (to in names.indices) {
        expected[from][to] = minOf(expected[from][to], expected[from][middle] + expected[middle][to])
    }
    for (from in names.indices) for (to in names.indices) {
        if (from == to) continue
        val route = TravelManager.findRoute(names[from], names[to]) ?: error("Missing route ${names[from]} -> ${names[to]}")
        check(route.totalHours == expected[from][to])
        var summedHours = 0
        var dangerousSegments = 0
        route.path.zipWithNext().forEach { (a, b) ->
            val road = roads.firstOrNull { (it.from == a && it.to == b) || (it.from == b && it.to == a) }
                ?: error("Route contains a nonexistent road: $a -> $b")
            summedHours += road.hours
            if (road.dangerous) dangerousSegments++
        }
        check(summedHours == route.totalHours)
        check(dangerousSegments == route.dangerousSegments)
    }

    println("Travel routes, shortest paths, dangerous segments, canonical names and all-pairs reference checks passed")
}
