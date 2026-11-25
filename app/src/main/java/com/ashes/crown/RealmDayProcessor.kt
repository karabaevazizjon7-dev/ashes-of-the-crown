package com.ashes.crown

/** Snapshot of one province at the start of a daily settlement. */
internal data class ProvinceDayState(
    val prosperity: Int,
    val loyalty: Int,
    val steward: Boolean,
    val crisisDays: Int
)

/** Result of resolving one in-game day. This class contains no Android/UI state. */
internal data class RealmDayResult(
    val armySize: Int,
    val provisions: Int,
    val guard: Int,
    val gold: Int,
    val provinces: List<ProvinceDayState>,
    val rationsUsed: Int,
    val supplied: Boolean,
    val shortage: Int,
    val soldiersLost: Int,
    val grossProvinceIncome: Int,
    val stewardWagesPaid: Int,
    val unpaidStewards: Int,
    val provinceEconomyProcessed: Boolean
)

/**
 * Resolves army upkeep and provincial administration for exactly one day.
 * Keeping this deterministic makes multi-day travel and save/reload behavior
 * straightforward to test without the Compose screen.
 */
internal object RealmDayProcessor {
    fun processDay(
        armySize: Int,
        provisions: Int,
        guard: Int,
        gold: Int,
        provinces: List<ProvinceDayState>,
        processProvinceEconomy: Boolean = true
    ): RealmDayResult {
        val safeArmy = armySize.coerceIn(0, 100)
        var nextArmy = safeArmy
        var nextProvisions = provisions.coerceIn(0, 100)
        var nextGuard = guard.coerceIn(0, 100)
        var nextGold = gold.coerceAtLeast(0).toLong()
        var rationsUsed = 0
        var supplied = false
        var shortage = 0
        var soldiersLost = 0

        val requiredRations = GameRules.dailyArmyRations(safeArmy)
        if (requiredRations > 0) {
            if (nextProvisions >= requiredRations) {
                nextProvisions -= requiredRations
                rationsUsed = requiredRations
                supplied = true
            } else {
                shortage = requiredRations - nextProvisions
                nextProvisions = 0
                soldiersLost = GameRules.soldiersLostFromShortage(shortage, nextArmy)
                nextArmy = (nextArmy - soldiersLost).coerceAtLeast(0)
                nextGuard = (nextGuard - 2).coerceAtLeast(0)
            }
        }

        var nextProvinces = provinces.map { province ->
            ProvinceDayState(
                prosperity = province.prosperity.coerceIn(0, 100),
                loyalty = (province.loyalty - if (shortage > 0) 1 else 0).coerceIn(0, 100),
                steward = province.steward,
                crisisDays = province.crisisDays.coerceIn(0, 3)
            )
        }
        var grossIncome = 0
        var wagesPaid = 0
        var unpaidStewards = 0

        if (processProvinceEconomy) {
            nextProvinces = nextProvinces.map { province ->
                val nextCrisis = when {
                    province.loyalty < 20 -> (province.crisisDays + 1).coerceAtMost(3)
                    province.loyalty >= 25 -> (province.crisisDays - 1).coerceAtLeast(0)
                    else -> province.crisisDays
                }
                ProvinceDayState(
                    prosperity = if (nextCrisis >= 3) (province.prosperity - 1).coerceAtLeast(0) else province.prosperity,
                    loyalty = province.loyalty,
                    steward = province.steward,
                    crisisDays = nextCrisis
                )
            }
            grossIncome = nextProvinces.sumOf { province ->
                GameRules.provinceDailyRevenue(
                    prosperity = province.prosperity,
                    loyalty = province.loyalty,
                    hasSteward = province.steward,
                    isInRevolt = province.crisisDays >= 3
                )
            }
            nextGold += grossIncome.toLong()
            val stewardIndexes = nextProvinces.indices.filter { nextProvinces[it].steward }
            val paid = minOf(nextGold, stewardIndexes.size.toLong()).toInt()
            wagesPaid = paid
            nextGold -= paid.toLong()
            unpaidStewards = stewardIndexes.size - paid
            if (unpaidStewards == 0) {
                nextProvinces = nextProvinces.map { province ->
                    if (province.steward) province.copy(loyalty = (province.loyalty + 1).coerceAtMost(100)) else province
                }
            } else {
                val unpaidIndexes = stewardIndexes.drop(paid).toSet()
                nextProvinces = nextProvinces.mapIndexed { index, province ->
                    if (index in unpaidIndexes) province.copy(loyalty = (province.loyalty - 2).coerceAtLeast(0)) else province
                }
            }
        }

        return RealmDayResult(
            armySize = nextArmy,
            provisions = nextProvisions,
            guard = nextGuard,
            gold = nextGold.coerceIn(0L, ResourceRules.MAX_GOLD.toLong()).toInt(),
            provinces = nextProvinces,
            rationsUsed = rationsUsed,
            supplied = supplied,
            shortage = shortage,
            soldiersLost = soldiersLost,
            grossProvinceIncome = grossIncome,
            stewardWagesPaid = wagesPaid,
            unpaidStewards = unpaidStewards,
            provinceEconomyProcessed = processProvinceEconomy
        )
    }
}
