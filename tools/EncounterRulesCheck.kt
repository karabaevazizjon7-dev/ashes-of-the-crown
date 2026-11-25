package com.ashes.crown

fun main() {
    val safe = TravelManager.findRoute("Королевский замок", "Зимний сад")!!
    val safeResult = RandomEncounterManager.resolve(safe, 4, 12, 0)
    check(safeResult.chancePercent == 0)
    check(safeResult.encounter == TravelEncounter.None)

    val dangerous = TravelManager.findRoute("Королевский замок", "Порт Веймар")!!
    val first = RandomEncounterManager.resolve(dangerous, 4, 12, 3)
    val repeated = RandomEncounterManager.resolve(dangerous, 4, 12, 3)
    check(first == repeated) // callback retry cannot reroll the same trip
    check(first.chancePercent in 1..70)
    check(first.roll in 0..99)

    val night = RandomEncounterManager.resolve(dangerous, 4, 2, 3)
    check(night.chancePercent > first.chancePercent)

    val observed = (0 until 500).map { index ->
        RandomEncounterManager.resolve(dangerous, index + 1, index % 24, index).encounter
    }
    check(observed.any { it is TravelEncounter.Merchant })
    check(observed.any { it is TravelEncounter.Traveler })
    check(observed.any { it is TravelEncounter.Ambush })
    check(observed.any { it == TravelEncounter.None })

    observed.filterIsInstance<TravelEncounter.Merchant>().forEach {
        check(it.goldReward in 1..10)
        check(it.itemReward.isNotBlank())
    }
    observed.filterIsInstance<TravelEncounter.Ambush>().forEach {
        check(it.enemyHealth in 1..200)
        check(it.enemyName.isNotBlank())
    }
    println("Random encounter determinism, safe-road exclusion, day/night risk and all encounter outcomes passed")
}
