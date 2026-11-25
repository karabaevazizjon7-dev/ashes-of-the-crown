package com.ashes.crown

/** Backward-compatible facade; economy calculations are owned by EconomyManager. */
internal object GameRules {
    fun dailyArmyRations(armySize: Int): Int = EconomyManager.dailyArmyRations(armySize)
    fun soldiersLostFromShortage(shortage: Int, armySize: Int): Int = EconomyManager.soldiersLostFromShortage(shortage, armySize)
    fun provinceDailyRevenue(prosperity: Int, loyalty: Int, hasSteward: Boolean, isInRevolt: Boolean): Int =
        EconomyManager.provinceDailyRevenue(prosperity, loyalty, hasSteward, isInRevolt)
}
