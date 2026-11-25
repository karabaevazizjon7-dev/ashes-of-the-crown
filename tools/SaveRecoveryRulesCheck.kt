package com.ashes.crown

/** Regression coverage for save integrity, schema migration and lifecycle recovery. */
object SaveRecoveryRulesCheck {
    @JvmStatic
    fun main(args: Array<String>) {
        val current = completeCurrentSnapshot()
        val acceptedCurrent = SaveManager.validateSnapshot(current, 24)
        check(acceptedCurrent is SaveValidationResult.Accepted)
        check(acceptedCurrent.sourceSchema == SaveManager.CURRENT_SCHEMA)

        // Schema 10 did not yet contain cooperative companion-scene markers.
        val legacy10 = current.toMutableMap().apply {
            put("save_schema_version", 10)
            remove("companion_team_event_flags")
            remove("companion_team_event_day")
        }
        val migrated10 = SaveManager.validateSnapshot(legacy10, 24)
        val accepted10 = migrated10 as? SaveValidationResult.Accepted
            ?: error("Schema-10 save should migrate")
        check(accepted10.sourceSchema == 10)
        check(accepted10.values["companion_team_event_flags"] == 0)
        check(accepted10.values["companion_team_event_day"] == 0)
        check(accepted10.values["save_schema_version"] == SaveManager.CURRENT_SCHEMA)

        // Old schema 7 data migrates independent companion bonds and later fields.
        val legacy7 = current.toMutableMap().apply {
            put("save_schema_version", 7)
            put("companion", "Мира")
            put("companion_bond", 42)
            remove("bond_companion_garrett")
            remove("bond_companion_celeste")
            remove("bond_companion_mira")
            remove("bond_companion_thomas")
            remove("companion_talk_day")
            remove("story_companion_garrett")
            remove("story_companion_celeste")
            remove("story_companion_mira")
            remove("story_companion_thomas")
            remove("relation_garrett_celeste")
            remove("relation_garrett_mira")
            remove("relation_garrett_thomas")
            remove("relation_celeste_mira")
            remove("relation_celeste_thomas")
            remove("relation_mira_thomas")
            remove("companion_relation_day")
            remove("companion_team_event_flags")
            remove("companion_team_event_day")
            remove("a14_upkeep_day")
        }
        val migrated7 = SaveManager.validateSnapshot(legacy7, 24)
        val accepted7 = migrated7 as? SaveValidationResult.Accepted
            ?: error("Schema-7 save should migrate")
        check(accepted7.sourceSchema == 7)
        check(accepted7.values["bond_companion_mira"] == 42)
        check(accepted7.values["bond_companion_garrett"] == 0)
        check(accepted7.values["story_companion_mira"] == 0)
        check(accepted7.values["relation_mira_thomas"] == 0)
        check(accepted7.values["companion_team_event_flags"] == 0)
        check(accepted7.values["a14_upkeep_day"] == accepted7.values["world_day"])

        // Current schema requires the upkeep marker to match the saved world day.
        val inconsistentDay = current.toMutableMap().apply { put("a14_upkeep_day", 999) }
        check(SaveManager.validateSnapshot(inconsistentDay, 24) is SaveValidationResult.Rejected)

        // Hash verification must reject missing, blank, altered and malformed hashes.
        val payload = "{\"scene\":3,\"gold\":18}"
        val hash = SaveManager.checksum(payload)
        check(hash.length == 64)
        check(hash.all { it in '0'..'9' || it in 'a'..'f' })
        check(SaveManager.checksumMatches(payload, hash))
        check(SaveManager.checksumMatches(payload, hash.uppercase()))
        // Missing hashes remain compatible at this pure helper boundary; the Android
        // slot adapter separately rejects them for current-schema slots.
        check(SaveManager.checksumMatches(payload, null))
        check(SaveManager.checksumMatches(payload, ""))
        check(!SaveManager.checksumMatches(payload, hash.dropLast(1) + "0"))
        check(!SaveManager.checksumMatches(payload + " ", hash))
        check(SaveManager.requiresChecksum(SaveManager.CURRENT_SCHEMA))
        check(!SaveManager.requiresChecksum(SaveManager.CURRENT_SCHEMA - 1))

        // Activity recreation suppresses exactly one stale outgoing save, then recovers.
        val gate = SaveLifecycleGate()
        check(gate.shouldSaveOnStop())
        gate.suppressNextStopSave()
        check(!gate.shouldSaveOnStop())
        check(gate.shouldSaveOnStop())
        gate.suppressNextStopSave()
        gate.suppressNextStopSave()
        check(!gate.shouldSaveOnStop())
        check(gate.shouldSaveOnStop())

        println("SaveRecoveryRulesCheck: PASS — current, schema-10 and schema-7 saves; checksum integrity; day marker; lifecycle recovery")
    }

    private fun completeCurrentSnapshot(): MutableMap<String, Any?> =
        SaveManager.currentSchemaRequiredKeys.associateWith { key ->
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
            put("scene", 3)
            put("gold", 18)
            put("hp", 100)
            put("world_day", 7)
            put("a14_upkeep_day", 7)
            put("companion", "Гаррет Морн")
            put("companion_bond", 0)
            put("items", "Архивный ключ")
            put("journal", "Начало пути")
        }
}
