package com.ashes.crown

fun main() {
    val names = listOf("Королевские земли", "Северная марка", "Портовые владения", "Золотые поля")
    val first = CrisisConsequenceManager.resolve(names, listOf(0, 0, 0, 0), listOf(1, 0, 0, 0))
    check(first.size == 1)
    check(first.single().to == ProvinceCrisisStage.UNREST)
    check(first.single().crownDelta == -1)
    check(first.single().neighboringLoyaltyLoss == 0)

    val escalation = CrisisConsequenceManager.resolve(names, listOf(1, 0, 0, 0), listOf(2, 0, 0, 0)).single()
    check(escalation.crownDelta == -2)
    check(escalation.peopleDelta == -1)
    check(escalation.neighboringLoyaltyLoss == 1)
    check(CrisisConsequenceManager.affectedNeighbors(0, 4) == listOf(1, 2, 3))

    val revolt = CrisisConsequenceManager.resolve(names, listOf(2, 0, 0, 0), listOf(3, 0, 0, 0)).single()
    check(revolt.crownDelta == -4 && revolt.peopleDelta == 2 && revolt.roseDelta == 1)
    check(revolt.neighboringLoyaltyLoss == 2)
    check(CrisisConsequenceManager.affectedNeighbors(2, 4) == listOf(0))

    // No repeated effects while remaining in the same stage, and no effects on recovery.
    check(CrisisConsequenceManager.resolve(names, listOf(3, 0, 0, 0), listOf(3, 0, 0, 0)).isEmpty())
    check(CrisisConsequenceManager.resolve(names, listOf(2, 0, 0, 0), listOf(1, 0, 0, 0)).isEmpty())
    check(CrisisConsequenceManager.resolve(names, listOf(0, 0, 0, 0), listOf(9, 0, 0, 0)).single().to == ProvinceCrisisStage.REVOLT)

    val portRevolt = CrisisConsequenceManager.resolve(names, listOf(2, 0, 0, 0), listOf(2, 0, 3, 0)).single()
    check(portRevolt.provinceIndex == 2 && portRevolt.merchantDelta == -2)
    println("Crisis transition consequences, faction reactions, neighbor spillover and no-repeat guarantees passed")
}
