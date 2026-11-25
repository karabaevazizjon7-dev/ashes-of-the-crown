package com.ashes.crown

fun main() {
    val calm = WorldEventContext(
        day = 12, gold = 20, influence = 4, guard = 10,
        crownReputation = 0, merchantReputation = 0, peopleReputation = 0, roseReputation = 0,
        provinceLoyalty = listOf(70, 40), provinceProsperity = listOf(60, 20),
        provinceCrisisDays = listOf(0, 0)
    )
    val first = WorldEventsManager.resolve(calm)
    val second = WorldEventsManager.resolve(calm.copy())
    check(first == second) { "Daily event must be deterministic for the same campaign snapshot" }
    check(first.title.isNotBlank() && first.description.isNotBlank())
    val calmKinds = (1..100).map { day -> WorldEventsManager.resolve(calm.copy(day = day)).kind }.toSet()
    check(calmKinds.containsAll(setOf(
        WorldEventKind.TRADE_CARAVAN,
        WorldEventKind.ROYAL_PATROL,
        WorldEventKind.MARKET_FAIR,
        WorldEventKind.ROSE_RUMOR,
        WorldEventKind.QUIET_DAY
    ))) { "The daily seed should rotate through all calm-world event types" }
    check(first.goldDelta in -10..10)
    check(first.influenceDelta in -5..5)
    check(first.crownReputationDelta in -5..5)
    check(first.merchantReputationDelta in -5..5)
    check(first.peopleReputationDelta in -5..5)
    check(first.roseReputationDelta in -5..5)

    val crisis = calm.copy(
        day = 5,
        provinceCrisisDays = listOf(1, 4),
        provinceLoyalty = listOf(60, 20)
    )
    var crisisEventSeen = false
    repeat(500) { offset ->
        val event = WorldEventsManager.resolve(crisis.copy(day = crisis.day + offset))
        if (event.kind == WorldEventKind.PROVINCIAL_UNREST) {
            crisisEventSeen = true
            check(event.provinceIndex == 1)
            check(event.provinceLoyaltyDelta == -1)
        }
        check(event.provinceIndex == null || event.provinceIndex in crisis.provinceLoyalty.indices)
    }
    check(crisisEventSeen) { "A sustained province crisis should be eligible for a world event" }

    val noProvinces = WorldEventsManager.resolve(calm.copy(
        provinceLoyalty = emptyList(), provinceProsperity = emptyList(), provinceCrisisDays = emptyList()
    ))
    check(noProvinces.provinceIndex == null)

    // Every result remains deterministic at integer boundaries; arithmetic is applied by bounded managers.
    val extremes = WorldEventContext(
        Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE,
        Int.MIN_VALUE, Int.MAX_VALUE, Int.MIN_VALUE, Int.MAX_VALUE,
        listOf(Int.MIN_VALUE), listOf(Int.MAX_VALUE), listOf(Int.MAX_VALUE)
    )
    check(WorldEventsManager.resolve(extremes) == WorldEventsManager.resolve(extremes.copy()))
    println("World event determinism, crisis targeting, empty-province handling and boundary inputs passed")
}
