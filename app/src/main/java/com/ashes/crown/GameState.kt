package com.ashes.crown

/** Immutable realm snapshot. Compose remains the owner of live UI state for now. */
internal data class GameState(
    val worldDay: Int,
    val worldHour: Int,
    val gold: Int,
    val health: Int,
    val sceneIndex: Int,
    val inventory: List<String>,
    val journal: List<String>,
    val realm: Realm
) {
    data class Realm(
        val armySize: Int,
        val provisions: Int,
        val guard: Int,
        val provinces: List<ProvinceDayState>
    ) {
        fun apply(result: RealmDayResult): Realm = copy(
            armySize = result.armySize,
            provisions = result.provisions,
            guard = result.guard,
            provinces = result.provinces.toList()
        )
    }

    companion object {
        fun realmSnapshot(armySize: Int, provisions: Int, guard: Int, provinces: List<ProvinceDayState>) =
            Realm(armySize, provisions, guard, provinces.toList())
    }
}
