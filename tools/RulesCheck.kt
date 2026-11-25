package com.ashes.crown

import kotlin.random.Random

private fun legacyOneDayReference(
    armySize: Int,
    provisions: Int,
    guard: Int,
    gold: Int,
    provinces: List<ProvinceDayState>,
    processProvinceEconomy: Boolean
): RealmDayResult {
    var army = armySize
    var supplies = provisions
    var nextGuard = guard
    var nextGold = gold
    val rations = GameRules.dailyArmyRations(army)
    var used = 0
    var shortage = 0
    var lost = 0
    if (rations > 0) {
        if (supplies >= rations) {
            supplies -= rations
            used = rations
        } else {
            shortage = rations - supplies
            supplies = 0
            lost = GameRules.soldiersLostFromShortage(shortage, army)
            army = (army - lost).coerceAtLeast(0)
            nextGuard = (nextGuard - 2).coerceAtLeast(0)
        }
    }
    var nextProvinces = provinces.map { province ->
        province.copy(loyalty = (province.loyalty - if (shortage > 0) 1 else 0).coerceAtLeast(0))
    }
    var gross = 0
    var paid = 0
    var unpaid = 0
    if (processProvinceEconomy) {
        nextProvinces = nextProvinces.map { province ->
            val crisis = when {
                province.loyalty < 20 -> (province.crisisDays + 1).coerceAtMost(3)
                province.loyalty >= 25 -> (province.crisisDays - 1).coerceAtLeast(0)
                else -> province.crisisDays
            }
            province.copy(
                prosperity = if (crisis >= 3) (province.prosperity - 1).coerceAtLeast(0) else province.prosperity,
                crisisDays = crisis
            )
        }
        gross = nextProvinces.sumOf { p -> GameRules.provinceDailyRevenue(p.prosperity, p.loyalty, p.steward, p.crisisDays >= 3) }
        nextGold += gross
        val stewardIndexes = nextProvinces.indices.filter { nextProvinces[it].steward }
        paid = minOf(nextGold, stewardIndexes.size)
        nextGold -= paid
        unpaid = stewardIndexes.size - paid
        if (unpaid == 0) {
            nextProvinces = nextProvinces.map { p -> if (p.steward) p.copy(loyalty = (p.loyalty + 1).coerceAtMost(100)) else p }
        } else {
            val unpaidIndexes = stewardIndexes.drop(paid).toSet()
            nextProvinces = nextProvinces.mapIndexed { index, p -> if (index in unpaidIndexes) p.copy(loyalty = (p.loyalty - 2).coerceAtLeast(0)) else p }
        }
    }
    return RealmDayResult(army, supplies, nextGuard, nextGold, nextProvinces, used, used > 0, shortage, lost, gross, paid, unpaid, processProvinceEconomy)
}

fun main() {
    // Combat calculations and reward eligibility.
    check(BattleRules.woundAttackPenalty(true) == 3)
    check(BattleRules.woundIncomingDamageBonus(true) == 2)
    check(BattleRules.armorValue(true, true, 10) == 25)
    check(BattleRules.incomingDamage(12, 5, false) == 7)
    check(BattleRules.incomingDamage(12, 5, true) == 1)
    check(BattleRules.defeatGoldLoss(0) == 0)
    check(BattleRules.defeatGoldLoss(100) == 10)
    check(BattleRules.defeatGoldLoss(20) == 3)

    val mercenary = BattleRules.rewardPlan("Наёмник Чёрной розы", false, 0)
    check(mercenary.eligible && mercenary.experience == 35 && mercenary.gold == 5 && mercenary.reputation == 1)
    check(mercenary.countsTowardDailyLimit)
    val ordinaryAtLimit = BattleRules.rewardPlan("Городской головорез", false, 4)
    check(!ordinaryAtLimit.eligible && ordinaryAtLimit.experience == 0 && ordinaryAtLimit.gold == 0)
    val bossFirst = BattleRules.rewardPlan("Древний рыцарь Чёрной розы", true, 4)
    check(bossFirst.eligible && bossFirst.experience == 90 && bossFirst.gold == 25)
    check(!bossFirst.countsTowardDailyLimit && bossFirst.isFirstUniqueBossVictory)
    val bossRepeat = BattleRules.rewardPlan("Древний рыцарь Чёрной розы", false, 0)
    check(!bossRepeat.eligible && bossRepeat.experience == 0 && bossRepeat.gold == 0)

    // Battle manager and equipment profile: all UI combat calculations use these pure rules.
    val baseAttack = BattleManager.playerAttack(10, 7, 3, 3, 25)
    check(baseAttack.damage == 20 && baseAttack.enemyHealthAfter == 5 && !baseAttack.defeated)
    val lethalAttack = BattleManager.playerAttack(30, 7, 10, 0, 20)
    check(lethalAttack.damage == 57 && lethalAttack.enemyHealthAfter == 0 && lethalAttack.defeated)
    val defeat = BattleManager.defeat(100, 8, 7)
    check(defeat.goldLoss == 10 && defeat.healthAfter == 35 && defeat.woundedUntilDay == 9)
    check(BattleManager.defeat(0, 4, 0).goldLoss == 0)
    val equipped = EquipmentManager.profile(listOf("Охотничий клинок", "Тяжёлый палаш", "Кольчуга стражника"))
    check(equipped.weaponName == "Тяжёлый палаш" && equipped.attackBonus == 7)
    check(equipped.armorName == "Кольчуга стражника" && equipped.armorBonus == 3)
    val defaultGear = EquipmentManager.profile(emptyList())
    check(defaultGear.attackBonus == 1 && defaultGear.armorBonus == 0)
    check(BattleManager.enemyAttack(12, 5, false) == 7)
    check(BattleManager.enemyAttack(12, 5, true) == 1)

    // Economy primitives and hostile/corrupt values.
    check(GameRules.dailyArmyRations(24) == 3)
    check(GameRules.dailyArmyRations(100) == 10)
    check(GameRules.dailyArmyRations(Int.MAX_VALUE) == 10)
    check(GameRules.soldiersLostFromShortage(2, 10) == 1)
    check(GameRules.soldiersLostFromShortage(Int.MAX_VALUE, Int.MAX_VALUE) == 50)
    check(GameRules.provinceDailyRevenue(50, 80, true, false) == 6)
    check(GameRules.provinceDailyRevenue(50, 80, true, true) == 0)
    check(GameRules.provinceDailyRevenue(Int.MAX_VALUE, Int.MAX_VALUE, false, false) == 6)

    // SaveManager validates a detached primitive snapshot before any active state is replaced.
    val validLegacySave = mapOf<String, Any?>(
        "scene" to 999, "gold" to -25, "hp" to 140, "world_day" to 8,
        "items" to "Ключ", "journal" to "Запись", "skill_power" to 99,
        "world_investigation_flags" to 31,
        "dialog_stage_garrett" to 9, "dialog_trust_garrett" to -999,
        "location" to "Порт Эстервала"
    )
    val acceptedLegacy = SaveManager.validateSnapshot(validLegacySave, maxSceneIndex = 20)
    check(acceptedLegacy is SaveValidationResult.Accepted)
    check(acceptedLegacy.sourceSchema == 1)
    check(acceptedLegacy.values["save_schema_version"] == SaveManager.CURRENT_SCHEMA)
    check(acceptedLegacy.values["scene"] == 20)
    check(acceptedLegacy.values["gold"] == 0)
    check(acceptedLegacy.values["hp"] == 100)
    check(acceptedLegacy.values["skill_power"] == 10)
    check(acceptedLegacy.values["world_investigation_flags"] == 15)
    check(acceptedLegacy.values["dialog_stage_garrett"] == 3)
    check(acceptedLegacy.values["dialog_trust_garrett"] == -100)
    check(acceptedLegacy.values["location"] == "Порт Веймар")
    check(validLegacySave["scene"] == 999) // input is immutable
    val invalidLocationSave = SaveManager.validateSnapshot(
        mapOf<String, Any?>("scene" to 1, "gold" to 0, "hp" to 50, "world_day" to 1,
            "items" to "", "journal" to "", "location" to "Локация вне карты"), 20
    )
    check(invalidLocationSave is SaveValidationResult.Accepted)
    check(invalidLocationSave.values["location"] == "Королевский замок")
    val negativeInvestigationFlags = SaveManager.validateSnapshot(
        mapOf<String, Any?>("scene" to 1, "gold" to 0, "hp" to 50, "world_day" to 1,
            "items" to "", "journal" to "", "world_investigation_flags" to -7), 20
    )
    check(negativeInvestigationFlags is SaveValidationResult.Accepted)
    check(negativeInvestigationFlags.values["world_investigation_flags"] == 0)

    // V47: legacy relationship values and progression counters are bounded on manual-slot import.
    val v47Bounds = SaveManager.validateSnapshot(
        mapOf<String, Any?>(
            "scene" to 1, "gold" to 10, "hp" to 50, "world_day" to 1, "items" to "", "journal" to "",
            "bond_thomas" to 900, "bond_mira" to -900, "bond_reynar" to 140,
            "social_conflict" to 99, "mem_garrett" to -4, "mem_seleste" to 2_000_000,
            "mem_thomas" to 7, "mem_mira" to 8, "events" to -1,
            "crafted_count" to 2_000_000, "encounters" to 3
        ), 20
    )
    check(v47Bounds is SaveValidationResult.Accepted)
    check(v47Bounds.values["bond_thomas"] == 100)
    check(v47Bounds.values["bond_mira"] == -100)
    check(v47Bounds.values["bond_reynar"] == 100)
    check(v47Bounds.values["social_conflict"] == 3)
    check(v47Bounds.values["mem_garrett"] == 0)
    check(v47Bounds.values["mem_seleste"] == 1_000_000)
    check(v47Bounds.values["events"] == 0)
    check(v47Bounds.values["crafted_count"] == 1_000_000)

    val schemaSevenCompanionSave = SaveManager.validateSnapshot(
        mapOf<String, Any?>(
            "save_schema_version" to 7, "scene" to 1, "gold" to 15, "hp" to 80,
            "world_day" to 9, "items" to "", "journal" to "",
            "companion" to "Мира", "companion_bond" to 37,
            "bond_companion_celeste" to 12, "bond_companion_thomas" to -4
        ), 20
    )
    check(schemaSevenCompanionSave is SaveValidationResult.Accepted)
    check(schemaSevenCompanionSave.sourceSchema == 7)
    check(schemaSevenCompanionSave.values["save_schema_version"] == 11)
    check(schemaSevenCompanionSave.values["bond_companion_garrett"] == 0)
    check(schemaSevenCompanionSave.values["bond_companion_celeste"] == 12)
    check(schemaSevenCompanionSave.values["bond_companion_mira"] == 37)
    check(schemaSevenCompanionSave.values["bond_companion_thomas"] == -4)
    check(schemaSevenCompanionSave.values["companion_talk_day"] == 0)
    check(schemaSevenCompanionSave.values["story_companion_garrett"] == 0)
    check(schemaSevenCompanionSave.values["story_companion_celeste"] == 0)
    check(schemaSevenCompanionSave.values["story_companion_mira"] == 0)
    check(schemaSevenCompanionSave.values["story_companion_thomas"] == 0)
    check(schemaSevenCompanionSave.values["relation_garrett_celeste"] == 0)
    check(schemaSevenCompanionSave.values["relation_garrett_mira"] == 0)
    check(schemaSevenCompanionSave.values["relation_garrett_thomas"] == 0)
    check(schemaSevenCompanionSave.values["relation_celeste_mira"] == 0)
    check(schemaSevenCompanionSave.values["relation_celeste_thomas"] == 0)
    check(schemaSevenCompanionSave.values["relation_mira_thomas"] == 0)
    check(schemaSevenCompanionSave.values["companion_relation_day"] == 0)
    val schemaNineSave = SaveManager.validateSnapshot(
        mapOf<String, Any?>("save_schema_version" to 9, "scene" to 1, "gold" to 10, "hp" to 50,
            "world_day" to 4, "items" to "", "journal" to "", "relation_garrett_mira" to 18), 20
    )
    check(schemaNineSave is SaveValidationResult.Accepted)
    check(schemaNineSave.values["save_schema_version"] == 11)
    check(schemaNineSave.values["relation_garrett_mira"] == 18)
    check(schemaNineSave.values["relation_mira_thomas"] == 0)
    check(schemaNineSave.values["companion_team_event_flags"] == 0)
    check(schemaNineSave.values["companion_team_event_day"] == 0)
    val schemaTenTeamSave = SaveManager.validateSnapshot(
        mapOf<String, Any?>("save_schema_version" to 10, "scene" to 2, "gold" to 22, "hp" to 70,
            "world_day" to 12, "items" to "", "journal" to "", "relation_garrett_mira" to 34,
            "companion_team_event_flags" to 5, "companion_team_event_day" to 11), 20
    )
    check(schemaTenTeamSave is SaveValidationResult.Accepted)
    check(schemaTenTeamSave.values["save_schema_version"] == 11)
    check(schemaTenTeamSave.values["relation_garrett_mira"] == 34)
    check(schemaTenTeamSave.values["companion_team_event_flags"] == 5)
    check(schemaTenTeamSave.values["companion_team_event_day"] == 11)

    val missingSave = SaveManager.validateSnapshot(mapOf("scene" to 1), 20)
    check(missingSave is SaveValidationResult.Rejected)
    val fractionalSave = SaveManager.validateSnapshot(
        mapOf("save_schema_version" to 7.5, "scene" to 1, "gold" to 1, "hp" to 50, "world_day" to 1, "items" to "", "journal" to ""), 20
    )
    check(fractionalSave is SaveValidationResult.Rejected)
    val fractionalCampaignValue = SaveManager.validateSnapshot(
        mapOf("scene" to 1, "gold" to 12.25, "hp" to 50, "world_day" to 1, "items" to "", "journal" to ""), 20
    )
    check(fractionalCampaignValue is SaveValidationResult.Rejected)
    val nonFiniteSave = SaveManager.validateSnapshot(
        mapOf("scene" to 1, "gold" to Double.NaN, "hp" to 50, "world_day" to 1, "items" to "", "journal" to ""), 20
    )
    check(nonFiniteSave is SaveValidationResult.Rejected)
    val malformedSave = SaveManager.validateSnapshot(
        mapOf("scene" to 1, "gold" to "many", "hp" to 50, "world_day" to 1, "items" to "", "journal" to ""), 20
    )
    check(malformedSave is SaveValidationResult.Rejected)
    val nestedSave = SaveManager.validateSnapshot(
        mapOf("scene" to 1, "gold" to 1, "hp" to 50, "world_day" to 1, "items" to "", "journal" to "", "unexpected" to listOf(1, 2)), 20
    )
    check(nestedSave is SaveValidationResult.Rejected)
    val futureSave = SaveManager.validateSnapshot(
        mapOf("save_schema_version" to (SaveManager.CURRENT_SCHEMA + 1), "scene" to 1, "gold" to 1, "hp" to 50, "world_day" to 1, "items" to "", "journal" to ""), 20
    )
    check(futureSave is SaveValidationResult.Rejected)
    val checksumPayload = "{\"scene\":1,\"gold\":2}"
    val checksum = SaveManager.checksum(checksumPayload)
    check(checksum.length == 64)
    check(SaveManager.checksum("{}") == "44136fa355b3678a1146ad16f7e8649e94fb4fc21fe77e8310c060f61caaff8a")
    check(SaveManager.checksumMatches(checksumPayload, checksum))
    check(!SaveManager.checksumMatches(checksumPayload + " ", checksum))
    check(SaveManager.checksumMatches(checksumPayload, null)) // legacy slots without hashes remain loadable

    // Save-list parsing preserves province positions despite malformed entries.
    check(SaveDataRules.parseBoundedIntList("10,bad,300", listOf(1, 2, 3, 4), 0, 100) == listOf(10, 2, 100, 4))
    check(SaveDataRules.parseBoundedIntList("-99,1", listOf(7, 8, 9, 10), 0, 100) == listOf(0, 1, 9, 10))
    check(SaveDataRules.normalizeDayMarker(99, 10) == 10)
    check(SaveDataRules.normalizeDayMarker(-5, 10) == 0)
    check(SaveDataRules.normalizeDayMarker(0, 10, allowZero = false) == 1)

    // One fully supplied day: rations, revenue, payroll and loyalty are coherent.
    val suppliedDay = RealmDayProcessor.processDay(
        armySize = 24, provisions = 3, guard = 10, gold = 10,
        provinces = listOf(
            ProvinceDayState(50, 80, true, 0),
            ProvinceDayState(50, 50, false, 0)
        )
    )
    check(suppliedDay.armySize == 24 && suppliedDay.provisions == 0)
    check(suppliedDay.supplied && suppliedDay.rationsUsed == 3)
    check(suppliedDay.grossProvinceIncome == 9 && suppliedDay.stewardWagesPaid == 1)
    check(suppliedDay.gold == 18 && suppliedDay.provinces[0].loyalty == 81)

    // Shortage causes bounded desertion, guard loss and local loyalty pressure.
    val shortageDay = RealmDayProcessor.processDay(
        armySize = 100, provisions = 0, guard = 20, gold = 0,
        provinces = listOf(ProvinceDayState(50, 20, false, 0))
    )
    check(shortageDay.shortage == 10 && shortageDay.soldiersLost == 5)
    check(shortageDay.armySize == 95 && shortageDay.guard == 18)
    check(shortageDay.provinces.single().loyalty == 19)
    check(shortageDay.provinces.single().crisisDays == 1)

    // Unpaid stewards lose loyalty; revolting provinces pay no income.
    val unpaidDay = RealmDayProcessor.processDay(
        armySize = 0, provisions = 0, guard = 0, gold = 0,
        provinces = listOf(
            ProvinceDayState(10, 0, true, 3),
            ProvinceDayState(10, 0, true, 3)
        )
    )
    check(unpaidDay.grossProvinceIncome == 0 && unpaidDay.stewardWagesPaid == 0)
    check(unpaidDay.unpaidStewards == 2)
    check(unpaidDay.provinces.all { it.loyalty == 0 && it.prosperity == 9 })

    // If a legacy income marker is already current, the same day is not paid twice.
    val upkeepOnly = RealmDayProcessor.processDay(
        armySize = 10, provisions = 1, guard = 5, gold = 7,
        provinces = listOf(ProvinceDayState(60, 60, true, 0)),
        processProvinceEconomy = false
    )
    check(upkeepOnly.provisions == 0 && upkeepOnly.gold == 7)
    check(!upkeepOnly.provinceEconomyProcessed && upkeepOnly.grossProvinceIncome == 0)

    // Differential test: the extracted processor must preserve the prior day-settlement
    // behavior for valid state values across a broad deterministic sample.
    repeat(2_000) { seed ->
        val random = Random(seed)
        val army = random.nextInt(0, 101)
        val supplies = random.nextInt(0, 101)
        val guard = random.nextInt(0, 101)
        val gold = random.nextInt(0, 101)
        val provinces = List(4) {
            ProvinceDayState(
                prosperity = random.nextInt(0, 101),
                loyalty = random.nextInt(0, 101),
                steward = random.nextBoolean(),
                crisisDays = random.nextInt(0, 4)
            )
        }
        val processEconomy = random.nextBoolean()
        val expected = legacyOneDayReference(army, supplies, guard, gold, provinces, processEconomy)
        val actual = RealmDayProcessor.processDay(army, supplies, guard, gold, provinces, processEconomy)
        check(actual == expected) { "Realm-day mismatch for deterministic seed $seed: expected=$expected actual=$actual" }
    }

    println("Battle, economy, save validation/integrity, save normalization and realm-day checks passed (including 2,000 differential cases)")
}
