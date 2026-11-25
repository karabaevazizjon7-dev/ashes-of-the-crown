package com.ashes.crown

/** Canonical location catalogue and deterministic travel validation. */
internal data class WorldLocation(
    val name: String,
    val defaultTravelHours: Int,
    val minimumScene: Int = 0,
    val minimumRoseQuestStage: Int = 0,
    val dangerousRoad: Boolean = false
)

internal sealed class TravelPlan {
    data class Ready(
        val origin: String,
        val destination: String,
        val hours: Int,
        val dangerousRoad: Boolean,
        val arrivalNote: String
    ) : TravelPlan()
    data class Rejected(val reason: String) : TravelPlan()
}

/**
 * World rules are deliberately independent from Compose and persistence.
 * This first extraction centralizes canonical names, unlocks and travel costs;
 * the UI remains responsible for applying time and saving the resulting state.
 */
internal object WorldManager {
    private val locations = listOf(
        WorldLocation("Королевский замок", 2),
        WorldLocation("Зимний сад", 1),
        WorldLocation("Старый мост", 2, minimumScene = 5, dangerousRoad = true),
        WorldLocation("Нижний город", 2),
        WorldLocation("Рыночная площадь", 2),
        WorldLocation("Порт Веймар", 3, minimumScene = 7, dangerousRoad = true),
        WorldLocation("Старая часовня", 3, minimumRoseQuestStage = 1, dangerousRoad = true),
        WorldLocation("Северная застава", 4, minimumScene = 5, dangerousRoad = true),
        WorldLocation("Воронья деревня", 3, minimumScene = 5, dangerousRoad = true),
        WorldLocation("Лесной тракт", 2, minimumScene = 5, dangerousRoad = true),
        WorldLocation("Крепость Вальден", 6, minimumScene = 5, dangerousRoad = true)
    )

    fun canonicalName(name: String): String = when (name.trim()) {
        "Порт Эстервала" -> "Порт Веймар"
        else -> name.trim()
    }

    fun catalogue(): List<WorldLocation> = locations.toList()

    fun location(name: String): WorldLocation? {
        val canonical = canonicalName(name)
        return locations.firstOrNull { it.name == canonical }
    }

    /** Repairs a persisted location to a known map node; legacy aliases stay supported. */
    fun normalizeLocation(name: String?): String {
        val candidate = name?.let(::canonicalName).orEmpty()
        return location(candidate)?.name ?: "Королевский замок"
    }

    fun isUnlocked(name: String, sceneIndex: Int, roseQuestStage: Int): Boolean {
        val place = location(name) ?: return false
        return sceneIndex >= place.minimumScene && roseQuestStage >= place.minimumRoseQuestStage
    }

    fun planTravel(
        currentLocation: String,
        destination: String,
        sceneIndex: Int,
        roseQuestStage: Int,
        requestedHours: Int? = null
    ): TravelPlan {
        val origin = canonicalName(currentLocation)
        if (location(origin) == null) {
            return TravelPlan.Rejected("Текущее местоположение не распознано. Вернись к известной точке карты.")
        }
        val target = location(destination)
            ?: return TravelPlan.Rejected("Это место отсутствует в известной карте королевства.")
        if (origin == target.name) return TravelPlan.Rejected("Ты уже находишься в этом месте.")
        if (!isUnlocked(target.name, sceneIndex, roseQuestStage)) {
            return TravelPlan.Rejected("Путь к месту «${target.name}» пока закрыт. Продвинься дальше по сюжету.")
        }
        val hours = (requestedHours ?: target.defaultTravelHours).coerceIn(0, 24)
        val dangerous = target.dangerousRoad
        val note = if (dangerous) {
            "Дорога к месту опасна: стоит быть готовым к неожиданностям."
        } else {
            "Путь пройден спокойно; местные жители продолжают обычные дела."
        }
        return TravelPlan.Ready(origin, target.name, hours, dangerous, note)
    }
}
