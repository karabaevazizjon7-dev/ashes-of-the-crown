package com.ashes.crown

fun main() {
    val calm = RealmPriorityManager.evaluate(
        gold = 80, provisions = 50, dailyRations = 2, army = 20, guard = 70,
        provinceNames = listOf("Север", "Порт"), provinceLoyalty = listOf(70, 65),
        provinceCrisisDays = listOf(0, 0), crownRep = 10, merchantRep = 10, peopleRep = 10, roseRep = 10
    )
    check(calm.isEmpty())

    val emergency = RealmPriorityManager.evaluate(
        gold = 0, provisions = 0, dailyRations = 3, army = 30, guard = 5,
        provinceNames = listOf("Королевские земли", "Портовые владения"),
        provinceLoyalty = listOf(8, 50), provinceCrisisDays = listOf(3, 0),
        crownRep = 50, merchantRep = -40, peopleRep = -50, roseRep = 60
    )
    check(emergency.size == 4)
    check(emergency.take(3).all { it.urgency == 3 })
    check(emergency.any { it.id == "army_supply" })
    check(emergency.any { it.id == "province_stability" })
    check(emergency.any { it.id == "treasury" })
    check(emergency.all { it.reason.isNotBlank() && it.recommendation.isNotBlank() })

    val limitedInputs = RealmPriorityManager.evaluate(
        gold = 20, provisions = 1, dailyRations = 0, army = 0, guard = 0,
        provinceNames = listOf("A", "B"), provinceLoyalty = listOf(5), provinceCrisisDays = listOf(0, 3),
        crownRep = -100, merchantRep = -100, peopleRep = 100, roseRep = -100
    )
    check(limitedInputs.none { it.id == "army_supply" })
    check(limitedInputs.size <= 4)
    println("Realm priorities: supply, treasury, province stability, faction tension and boundary cases passed")
}
