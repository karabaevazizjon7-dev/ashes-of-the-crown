package com.ashes.crown

/** Immutable campaign economy snapshot used by transactional realm actions. */
internal data class EconomyState(
    val gold: Int,
    val provisions: Int,
    val armySize: Int,
    val guard: Int,
    val treasuryLevel: Int
)

internal sealed class EconomyActionResult {
    data class Applied(val state: EconomyState, val message: String, val journalEntry: String? = null) : EconomyActionResult()
    data class Rejected(val message: String) : EconomyActionResult()
}

/** Pure economy rules. Each action validates every precondition before returning a new state. */
internal object EconomyManager {
    fun dailyArmyRations(armySize: Int): Int = (armySize.coerceIn(0, 100) + 9) / 10

    fun soldiersLostFromShortage(shortage: Int, armySize: Int): Int {
        val safeArmy = armySize.coerceIn(0, 100)
        val safeShortage = shortage.coerceIn(0, 100)
        return if (safeArmy == 0 || safeShortage == 0) 0
        else ((safeShortage + 1) / 2).coerceAtLeast(1).coerceAtMost(safeArmy)
    }

    fun provinceDailyRevenue(prosperity: Int, loyalty: Int, hasSteward: Boolean, isInRevolt: Boolean): Int {
        if (isInRevolt) return 0
        val safeLoyalty = loyalty.coerceIn(0, 100)
        val safeProsperity = prosperity.coerceIn(0, 100)
        val loyaltyModifier = when { safeLoyalty >= 75 -> 1; safeLoyalty < 25 -> -1; else -> 0 }
        return (1 + safeProsperity / 25 + loyaltyModifier + if (hasSteward) 2 else 0).coerceAtLeast(0)
    }

    fun provinceForecast(provinces: List<ProvinceDayState>): Int = provinces.sumOf { province ->
        provinceDailyRevenue(province.prosperity, province.loyalty, province.steward, province.crisisDays >= 3)
    }

    fun buyProvisions(state: EconomyState): EconomyActionResult {
        val safe = normalize(state)
        return when {
            safe.provisions >= 100 -> EconomyActionResult.Rejected("Склады заполнены: запас провианта уже достиг 100 порций.")
            safe.provisions > 90 -> EconomyActionResult.Rejected("На складах осталось место менее чем для 10 порций. Сначала используй часть запасов.")
            safe.gold < 6 -> EconomyActionResult.Rejected("Не хватает 6 золотых.")
            else -> EconomyActionResult.Applied(
                safe.copy(gold = safe.gold - 6, provisions = safe.provisions + 10),
                "Закуплено 10 порций провианта. Запас: ${safe.provisions + 10}/100.",
                "Снабжение: закуплено 10 порций за 6 золотых."
            )
        }
    }

    fun recruit(state: EconomyState): EconomyActionResult {
        val safe = normalize(state)
        return when {
            safe.armySize >= 100 -> EconomyActionResult.Rejected("Королевское войско достигло предельной численности — 100 солдат.")
            safe.armySize > 95 -> EconomyActionResult.Rejected("Для полного набора пяти солдат не хватает свободных мест. Максимум — 100.")
            safe.gold < 12 || safe.provisions < 3 -> EconomyActionResult.Rejected("Для набора нужно 12 золота и 3 порции провианта.")
            else -> EconomyActionResult.Applied(
                safe.copy(gold = safe.gold - 12, provisions = safe.provisions - 3, armySize = safe.armySize + 5),
                "Пять новых бойцов принесли присягу короне. Войско: ${safe.armySize + 5}/100.",
                "Армия: набрано 5 солдат."
            )
        }
    }

    fun trainGuard(state: EconomyState): EconomyActionResult {
        val safe = normalize(state)
        return when {
            safe.guard >= 100 -> EconomyActionResult.Rejected("Гвардия уже достигла максимального уровня подготовки. Новые учения не требуются.")
            safe.provisions < 5 -> EconomyActionResult.Rejected("Для учений требуется 5 порций провианта.")
            else -> EconomyActionResult.Applied(
                safe.copy(provisions = safe.provisions - 5, guard = (safe.guard + 4).coerceAtMost(100)),
                "Гвардия провела учения. Доверие и боевой дух укрепились. Подготовка: ${(safe.guard + 4).coerceAtMost(100)}/100.",
                "Армия: проведены учения гвардии."
            )
        }
    }

    fun collectTaxes(state: EconomyState, lastTaxDay: Int, worldDay: Int, merchantRep: Int): Pair<EconomyActionResult, Int> {
        val safe = normalize(state)
        // A future marker can appear after a rolled-back/corrupted world clock.
        // Treat it as already collected rather than allowing repeated payouts.
        if (worldDay < 1 || lastTaxDay >= worldDay) {
            val message = if (worldDay < 1) "Нельзя собирать налоги до первого игрового дня." else "Налоги за этот день уже собраны. Подожди до следующего дня."
            return EconomyActionResult.Rejected(message) to lastTaxDay
        }
        // Reputation is defined on the -100..100 scale. Clamp before calculating
        // income so corrupted/extreme values cannot create an enormous payout.
        val safeMerchantRep = merchantRep.coerceIn(-100, 100)
        val income = 8 + safe.treasuryLevel * 4 + safeMerchantRep.coerceAtLeast(0) / 5
        val nextGold = (safe.gold.toLong() + income.toLong()).coerceAtMost(1_000_000L).toInt()
        return EconomyActionResult.Applied(safe.copy(gold = nextGold), "Сбор завершён. В казну поступило $income золотых. Торговые дома внесли свою долю.", "Казна: ежедневный сбор налогов +$income золота.") to worldDay
    }

    fun upgradeTreasury(state: EconomyState): EconomyActionResult {
        val safe = normalize(state)
        val cost = safe.treasuryLevel * 25
        return when {
            safe.treasuryLevel >= 3 -> EconomyActionResult.Rejected("Казначейство уже достигло максимального уровня.")
            safe.gold < cost -> EconomyActionResult.Rejected("Нужно $cost золотых для улучшения.")
            else -> EconomyActionResult.Applied(safe.copy(gold = safe.gold - cost, treasuryLevel = safe.treasuryLevel + 1), "Казначейство улучшено до уровня ${safe.treasuryLevel + 1}. Будущие сборы станут выше.", "Казна: улучшено казначейство до уровня ${safe.treasuryLevel + 1}.")
        }
    }

    private fun normalize(state: EconomyState) = state.copy(
        gold = state.gold.coerceIn(0, 1_000_000), provisions = state.provisions.coerceIn(0, 100),
        armySize = state.armySize.coerceIn(0, 100), guard = state.guard.coerceIn(0, 100), treasuryLevel = state.treasuryLevel.coerceIn(1, 3)
    )
}
