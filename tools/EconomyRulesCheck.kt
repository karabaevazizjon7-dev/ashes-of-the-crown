package com.ashes.crown

fun main() {
    val base = EconomyState(gold = 30, provisions = 20, armySize = 24, guard = 50, treasuryLevel = 1)
    val purchase = EconomyManager.buyProvisions(base) as EconomyActionResult.Applied
    check(purchase.state.gold == 24 && purchase.state.provisions == 30)
    check(base.gold == 30 && base.provisions == 20) // immutable input
    check(EconomyManager.buyProvisions(base.copy(gold = 5)) is EconomyActionResult.Rejected)
    check(EconomyManager.buyProvisions(base.copy(provisions = 95)) is EconomyActionResult.Rejected)
    check(EconomyManager.buyProvisions(base.copy(provisions = 100)) is EconomyActionResult.Rejected)

    val recruit = EconomyManager.recruit(base) as EconomyActionResult.Applied
    check(recruit.state.gold == 18 && recruit.state.provisions == 17 && recruit.state.armySize == 29)
    check(EconomyManager.recruit(base.copy(armySize = 96)) is EconomyActionResult.Rejected)
    check(EconomyManager.recruit(base.copy(gold = 11)) is EconomyActionResult.Rejected)
    check(EconomyManager.recruit(base.copy(provisions = 2)) is EconomyActionResult.Rejected)

    val trained = EconomyManager.trainGuard(base) as EconomyActionResult.Applied
    check(trained.state.provisions == 15 && trained.state.guard == 54)
    check(EconomyManager.trainGuard(base.copy(guard = 100)) is EconomyActionResult.Rejected)
    check(EconomyManager.trainGuard(base.copy(provisions = 4)) is EconomyActionResult.Rejected)

    val (tax, taxDay) = EconomyManager.collectTaxes(base, 4, 5, 10)
    check(tax is EconomyActionResult.Applied && tax.state.gold == 44 && taxDay == 5)
    check(EconomyManager.collectTaxes(base, 5, 5, 10).first is EconomyActionResult.Rejected)
    // A future tax marker (e.g. clock rollback/corrupt state) must not reopen collection.
    val (futureMarkerResult, futureMarkerDay) = EconomyManager.collectTaxes(base, 9, 5, 10)
    check(futureMarkerResult is EconomyActionResult.Rejected && futureMarkerDay == 9)
    check(EconomyManager.collectTaxes(base, 0, 0, 10).first is EconomyActionResult.Rejected)
    // Corrupt reputation must not turn taxes into an unbounded payout.
    val (extremeRepResult, _) = EconomyManager.collectTaxes(base.copy(gold = 0), 0, 1, Int.MAX_VALUE)
    check(extremeRepResult is EconomyActionResult.Applied && extremeRepResult.state.gold == 32)
    val (negativeRepResult, _) = EconomyManager.collectTaxes(base.copy(gold = 0), 0, 1, Int.MIN_VALUE)
    check(negativeRepResult is EconomyActionResult.Applied && negativeRepResult.state.gold == 12)
    val (cappedGoldResult, _) = EconomyManager.collectTaxes(base.copy(gold = 999_995), 0, 1, 100)
    check(cappedGoldResult is EconomyActionResult.Applied && cappedGoldResult.state.gold == 1_000_000)
    val upgrade = EconomyManager.upgradeTreasury(base) as EconomyActionResult.Applied
    check(upgrade.state.gold == 5 && upgrade.state.treasuryLevel == 2)
    check(EconomyManager.upgradeTreasury(base.copy(gold = 24)) is EconomyActionResult.Rejected)
    check(EconomyManager.upgradeTreasury(base.copy(treasuryLevel = 3)) is EconomyActionResult.Rejected)

    val provinces = listOf(ProvinceDayState(50, 80, true, 0), ProvinceDayState(40, 20, false, 3))
    check(EconomyManager.provinceForecast(provinces) == 6)
    val realm = GameState.realmSnapshot(24, 20, 50, provinces)
    val resolved = RealmDayProcessor.processDay(realm.armySize, realm.provisions, realm.guard, 30, realm.provinces)
    val updated = realm.apply(resolved)
    check(updated.armySize == resolved.armySize && updated.provisions == resolved.provisions)
    check(updated.provinces == resolved.provinces)
    check(realm.provisions == 20) // snapshots do not mutate

    println("EconomyManager transactions, treasury, provisioning, recruitment, training and GameState snapshots passed")
}
