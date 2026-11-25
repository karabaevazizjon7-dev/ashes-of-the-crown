package com.ashes.crown

/** Cross-system regression test for the same ordered daily pipeline used by MainActivity. */
fun main() {
    var day = 1
    var lastUpkeepDay = 1
    var provinceIncomeDay = 1
    var army = 24
    var provisions = 30
    var guard = 60
    var gold = 120
    var crown = 50
    var merchants = 50
    var people = 50
    var rose = 10
    val names = listOf("Королевские земли", "Северная марка", "Портовые владения", "Золотые поля")
    var provinces = listOf(
        ProvinceDayState(62, 70, true, 0),
        ProvinceDayState(38, 48, false, 0),
        ProvinceDayState(55, 58, false, 0),
        ProvinceDayState(68, 64, false, 0)
    )
    val processedEventDays = mutableListOf<Int>()
    var economyDays = 0
    var totalRations = 0

    // Simulate advancing from day 1 to day 7, then continuing after a save/reload boundary.
    val firstAdvance = WorldClockManager.advance(day, 9, 72, lastUpkeepDay)
    check(firstAdvance.targetDay == 4 && firstAdvance.pendingUpkeepDays == 3)
    val secondAdvance = WorldClockManager.advance(firstAdvance.targetDay, firstAdvance.targetHour, 72, lastUpkeepDay + firstAdvance.pendingUpkeepDays)
    check(secondAdvance.targetDay == 7 && secondAdvance.pendingUpkeepDays == 3)

    for (advance in listOf(firstAdvance, secondAdvance)) {
        repeat(advance.pendingUpkeepDays) {
            lastUpkeepDay++
            val before = provinces
            val daily = RealmDayProcessor.processDay(
                armySize = army,
                provisions = provisions,
                guard = guard,
                gold = gold,
                provinces = provinces,
                processProvinceEconomy = provinceIncomeDay < lastUpkeepDay
            )
            army = daily.armySize
            provisions = daily.provisions
            guard = daily.guard
            gold = daily.gold
            provinces = daily.provinces
            totalRations += daily.rationsUsed
            if (daily.provinceEconomyProcessed) {
                economyDays++
                provinceIncomeDay = lastUpkeepDay
            }
            val transitions = CrisisConsequenceManager.resolve(
                names, before.map { it.crisisDays }, provinces.map { it.crisisDays }
            )
            transitions.forEach {
                crown = FactionManager.applyDelta(crown, it.crownDelta)
                merchants = FactionManager.applyDelta(merchants, it.merchantDelta)
                people = FactionManager.applyDelta(people, it.peopleDelta)
                rose = FactionManager.applyDelta(rose, it.roseDelta)
            }
            val event = WorldEventsManager.resolve(
                WorldEventContext(lastUpkeepDay, gold, 20, guard, crown, merchants, people, rose,
                    provinces.map { it.loyalty }, provinces.map { it.prosperity }, provinces.map { it.crisisDays })
            )
            processedEventDays += lastUpkeepDay
            gold = (gold + event.goldDelta).coerceIn(0, 1_000_000)
            guard = (guard + event.guardDelta).coerceIn(0, 100)
            crown = FactionManager.applyDelta(crown, event.crownReputationDelta)
            merchants = FactionManager.applyDelta(merchants, event.merchantReputationDelta)
            people = FactionManager.applyDelta(people, event.peopleReputationDelta)
            rose = FactionManager.applyDelta(rose, event.roseReputationDelta)
        }
        day = advance.targetDay
    }

    check(day == 7 && lastUpkeepDay == 7)
    check(processedEventDays == listOf(2, 3, 4, 5, 6, 7))
    check(processedEventDays.distinct().size == processedEventDays.size)
    check(economyDays == 6 && provinceIncomeDay == 7)
    check(totalRations > 0 && provisions in 0..100 && army in 0..100)
    check(guard in 0..100 && gold in 0..1_000_000)
    check(listOf(crown, merchants, people, rose).all { it in 0..100 })
    check(provinces.all { it.loyalty in 0..100 && it.prosperity in 0..100 && it.crisisDays in 0..3 })

    // Re-entering the pipeline with the persisted marker must not settle a day twice.
    val replay = WorldClockManager.advance(day, 9, 0, lastUpkeepDay)
    check(replay.pendingUpkeepDays == 0)
    check(WorldEventsManager.resolve(WorldEventContext(7, gold, 20, guard, crown, merchants, people, rose,
        provinces.map { it.loyalty }, provinces.map { it.prosperity }, provinces.map { it.crisisDays })) ==
        WorldEventsManager.resolve(WorldEventContext(7, gold, 20, guard, crown, merchants, people, rose,
            provinces.map { it.loyalty }, provinces.map { it.prosperity }, provinces.map { it.crisisDays })))

    val cappedTreasury = RealmDayProcessor.processDay(
        armySize = 0, provisions = 0, guard = 0, gold = 999_999,
        provinces = listOf(
            ProvinceDayState(100, 100, true, 0), ProvinceDayState(100, 100, true, 0),
            ProvinceDayState(100, 100, true, 0), ProvinceDayState(100, 100, true, 0)
        )
    )
    check(cappedTreasury.gold == ResourceRules.MAX_GOLD)
    val overflowTreasury = RealmDayProcessor.processDay(
        armySize = 0, provisions = 0, guard = 0, gold = Int.MAX_VALUE,
        provinces = List(4) { ProvinceDayState(100, 100, true, 0) }
    )
    check(overflowTreasury.gold == ResourceRules.MAX_GOLD)
    println("RealmDayPipelineRulesCheck: PASS (6 daily settlements, 6 economy ticks, unique event days, reload marker)")
}
