package com.ashes.crown

/** Deterministic endurance checks for repeated world-day settlement and route planning. */
object LongCampaignStabilityRulesCheck {
    @JvmStatic fun main(args: Array<String>) {
        var day = 1
        var hour = 8
        var upkeepDay = 1
        var army = 40
        var provisions = 100
        var guard = 75
        var gold = 250
        var provinces = listOf(
            ProvinceDayState(72, 68, true, 0),
            ProvinceDayState(55, 42, false, 0),
            ProvinceDayState(30, 28, true, 0)
        )

        // Simulate a 180-day campaign, including clock rollover and one daily settlement.
        repeat(180) { index ->
            val advance = WorldClockManager.advance(day, hour, 16, upkeepDay)
            check(advance.elapsedHours == 16)
            check(advance.targetDay >= day)
            day = advance.targetDay
            hour = advance.targetHour
            if (advance.pendingUpkeepDays > 0) {
                repeat(advance.pendingUpkeepDays) {
                    val result = RealmDayProcessor.processDay(army, provisions, guard, gold, provinces)
                    army = result.armySize
                    provisions = result.provisions
                    guard = result.guard
                    gold = result.gold
                    provinces = result.provinces
                    upkeepDay++
                    check(army in 0..100 && provisions in 0..100 && guard in 0..100)
                    check(gold in 0..ResourceRules.MAX_GOLD)
                    check(provinces.all { it.prosperity in 0..100 && it.loyalty in 0..100 && it.crisisDays in 0..3 })
                }
            }
            check(upkeepDay == day || upkeepDay == day + 1)
            check(index >= 0)
        }
        check(day >= 120)

        // Every declared road must be traversable in both directions with equal duration.
        val roads = TravelManager.roadCatalogue()
        check(roads.isNotEmpty())
        roads.forEach { road ->
            val forward = TravelManager.findRoute(road.from, road.to)
                ?: error("Missing forward route: ${road.from} -> ${road.to}")
            val backward = TravelManager.findRoute(road.to, road.from)
                ?: error("Missing reverse route: ${road.to} -> ${road.from}")
            check(forward.totalHours == backward.totalHours)
            check(forward.path.first() == road.from && forward.path.last() == road.to)
            check(backward.path.first() == road.to && backward.path.last() == road.from)
        }

        // Extreme clock requests must remain bounded and non-negative.
        val extreme = WorldClockManager.advance(Int.MAX_VALUE - 2, 23, Int.MAX_VALUE, 0)
        check(extreme.targetDay == Int.MAX_VALUE)
        check(extreme.targetHour in 0..23)
        check(extreme.pendingUpkeepDays >= 0)
        println("LongCampaignStabilityRulesCheck: 180-day settlement, province/resource bounds, bidirectional roads and clock extremes passed")
    }
}
