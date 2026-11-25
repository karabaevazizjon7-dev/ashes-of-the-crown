package com.ashes.crown

/** A deterministic travel encounter selected from route and campaign context. */
internal sealed class TravelEncounter {
    data object None : TravelEncounter()
    data class Merchant(val goldReward: Int, val itemReward: String, val description: String) : TravelEncounter()
    data class Traveler(val description: String) : TravelEncounter()
    data class Ambush(val enemyName: String, val enemyHealth: Int, val description: String) : TravelEncounter()
}

internal data class EncounterResolution(
    val encounter: TravelEncounter,
    val chancePercent: Int,
    val roll: Int
)

/**
 * Pure encounter rules. The seed is derived from saved campaign context, so the
 * same trip resolves identically if a UI callback is retried before state changes.
 */
internal object RandomEncounterManager {
    fun resolve(
        route: TravelRoute,
        worldDay: Int,
        departureHour: Int,
        encounterCount: Int
    ): EncounterResolution {
        val night = departureHour !in 6..19
        val chance = if (route.dangerousSegments == 0) 0 else
            (12 + route.dangerousSegments * 18 + if (night) 8 else 0).coerceIn(0, 70)
        val seedText = "${route.path.joinToString("|")}:$worldDay:$departureHour:$encounterCount"
        val hash = seedText.fold(1125899906842597L) { acc, char -> acc * 31L + char.code }
        val roll = Math.floorMod(hash, 100L).toInt()
        if (roll >= chance) return EncounterResolution(TravelEncounter.None, chance, roll)

        val kind = Math.floorMod(hash ushr 7, 100L).toInt()
        val encounter = when {
            kind < 28 -> TravelEncounter.Merchant(
                goldReward = 2,
                itemReward = "Дорожные припасы",
                description = "У обочины ты встречаешь торговца, который делится припасами и небольшой частью выручки."
            )
            kind < 58 -> TravelEncounter.Traveler(
                "У костра незнакомый путник рассказывает свежие слухи о дорогах и событиях в королевстве."
            )
            kind < 82 -> TravelEncounter.Ambush(
                "Дорожный разбойник", 25,
                "Из укрытия выходит дорожный разбойник и преграждает путь."
            )
            else -> TravelEncounter.Ambush(
                "Наёмник Чёрной розы", 48,
                "На дороге появляется наёмник Чёрной розы. Похоже, за тобой следили."
            )
        }
        return EncounterResolution(encounter, chance, roll)
    }
}
