package com.ashes.crown

fun main() {
    val stringFields = setOf("a11_report", "a13_loyalty", "a13_prosperity", "a13_report", "a13_stewards", "a13_tax_days", "a15_crisis_days", "battle_enemy", "battle_log", "companion", "companion_moment_report", "companion_relation_report", "companion_story_report", "companion_team_event_report", "council_report", "dialog_npc", "expedition_note", "expedition_report", "frontier_report", "items", "journal", "location", "outcome", "social_event", "world_event")
    val booleanFields = setOf("boss_defeated", "chosen", "quest_medicine_crate_found")
    val complete = SaveManager.currentSchemaRequiredKeys.associateWith { key ->
        when {
            key in stringFields -> ""
            key in booleanFields -> false
            else -> 0
        }
    }.toMutableMap()

    complete["save_schema_version"] = SaveManager.CURRENT_SCHEMA
    complete["scene"] = 0
    complete["gold"] = 18
    complete["hp"] = 100
    complete["world_day"] = 1
    complete["items"] = "Архивный ключ"
    complete["journal"] = "Начало пути"

    check(SaveManager.validateSnapshot(complete, 18) is SaveValidationResult.Accepted) {
        "A complete current-schema snapshot must be accepted"
    }

    val requiredWithoutSchema = SaveManager.currentSchemaRequiredKeys - "save_schema_version"
    requiredWithoutSchema.forEach { missingKey ->
        val incomplete = complete.toMutableMap().apply { remove(missingKey) }
        val result = SaveManager.validateSnapshot(incomplete, 18)
        check(result is SaveValidationResult.Rejected) {
            "Current-schema snapshot missing '$missingKey' was accepted"
        }
    }

    // Schema 10 snapshots remain eligible for the existing migration path.
    val legacy = mutableMapOf<String, Any?>(
        "save_schema_version" to 10,
        "scene" to 0,
        "gold" to 18,
        "hp" to 100,
        "world_day" to 1,
        "items" to "",
        "journal" to ""
    )
    check(SaveManager.validateSnapshot(legacy, 18) is SaveValidationResult.Accepted) {
        "Legacy schema 10 snapshot must retain backward compatibility"
    }
    val legacyWithoutUpkeepMarker = legacy.toMutableMap().apply { put("world_day", 40) }
    val migratedLegacy = SaveManager.validateSnapshot(legacyWithoutUpkeepMarker, 18)
    check(migratedLegacy is SaveValidationResult.Accepted)
    check(migratedLegacy.values["a14_upkeep_day"] == 40) {
        "A legacy save without a daily-settlement marker must start at its current day, not replay old days"
    }

    val inconsistentCurrent = complete.toMutableMap().apply { put("world_day", 40); put("a14_upkeep_day", 1) }
    check(SaveManager.validateSnapshot(inconsistentCurrent, 18) is SaveValidationResult.Rejected) {
        "Current schema must reject inconsistent world-day and upkeep-day markers"
    }

    check(SaveManager.requiresChecksum(SaveManager.CURRENT_SCHEMA)) {
        "Current schema must require an integrity checksum"
    }
    check(SaveManager.requiresChecksum(SaveManager.CURRENT_SCHEMA + 1)) {
        "Future schema markers must not silently bypass checksum policy"
    }
    check(!SaveManager.requiresChecksum(10)) {
        "Legacy schema 10 slots without historical checksums must remain loadable"
    }

    println("Current-schema completeness: ${requiredWithoutSchema.size} missing-field cases rejected; schema 11 checksum required; schema 10 migration and legacy checksum compatibility preserved")
}
