package com.ashes.crown

fun main() {
    val complete = SaveManager.currentSchemaRequiredKeys.associateWith { key ->
        when (key) {
            "social_event", "location", "world_event", "battle_enemy", "battle_log", "outcome",
            "dialog_npc", "journal", "items", "expedition_note", "council_report", "companion",
            "expedition_report", "companion_moment_report", "companion_story_report",
            "companion_relation_report", "companion_team_event_report", "frontier_report", "a11_report",
            "a13_prosperity", "a13_loyalty", "a13_stewards", "a13_tax_days", "a15_crisis_days", "a13_report" -> ""
            "chosen", "quest_medicine_crate_found", "boss_defeated" -> false
            else -> 0
        }
    }.toMutableMap<String, Any?>().apply {
        put("save_schema_version", SaveManager.CURRENT_SCHEMA)
        put("scene", 0); put("gold", 18); put("hp", 100); put("world_day", 1)
        put("items", "Архивный ключ"); put("journal", "Начало пути")
    }

    check(SaveManager.validateSnapshot(complete, 18) is SaveValidationResult.Accepted)

    val wrappedCurrentSchema = complete.toMutableMap().apply {
        put("save_schema_version", 4_294_967_307L) // Int narrowing previously wrapped this to 11.
    }
    check(SaveManager.validateSnapshot(wrappedCurrentSchema, 18) is SaveValidationResult.Rejected) {
        "A schema value that overflows Int must not masquerade as the current schema"
    }

    val tooLargeSchema = complete.toMutableMap().apply { put("save_schema_version", Int.MAX_VALUE.toLong() + 1L) }
    check(SaveManager.validateSnapshot(tooLargeSchema, 18) is SaveValidationResult.Rejected)

    val fractionalSchema = complete.toMutableMap().apply { put("save_schema_version", 11.5) }
    check(SaveManager.validateSnapshot(fractionalSchema, 18) is SaveValidationResult.Rejected)

    val negativeSchema = complete.toMutableMap().apply { put("save_schema_version", Long.MIN_VALUE) }
    check(SaveManager.validateSnapshot(negativeSchema, 18) is SaveValidationResult.Rejected)
    val missingSchema = complete.toMutableMap().apply { remove("save_schema_version") }
    check(SaveManager.validateSnapshot(missingSchema, 18) is SaveValidationResult.Rejected) {
        "A current snapshot must not downgrade into checksum-free legacy mode when its schema marker is removed"
    }
    val nullSchema = complete.toMutableMap().apply { put("save_schema_version", null) }
    check(SaveManager.validateSnapshot(nullSchema, 18) is SaveValidationResult.Rejected) {
        "An explicit null schema marker must not be interpreted as a legacy save"
    }

    println("Save schema boundary checks passed: valid current schema accepted; overflow, out-of-range, fractional and negative schema values rejected")
}
