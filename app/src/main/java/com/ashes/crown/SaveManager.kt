package com.ashes.crown

import java.security.MessageDigest

/** Result of validating and normalizing a manual campaign save. */
internal sealed class SaveValidationResult {
    data class Accepted(
        val values: Map<String, Any?>,
        val sourceSchema: Int
    ) : SaveValidationResult()

    data class Rejected(val reason: String) : SaveValidationResult()
}

/**
 * Pure save-slot rules. Android storage and Activity recreation stay at the UI boundary;
 * schema validation, primitive-type checks, numeric bounds and integrity hashing live here.
 */
internal object SaveManager {
    const val CURRENT_SCHEMA = 11

    private val requiredKeys = setOf("scene", "gold", "hp", "world_day", "items", "journal")

    /**
     * A schema-11 slot is a complete campaign snapshot, not a sparse patch.
     * Keep this registry in sync with MainActivity.save(): loading a partial
     * current-schema slot clears preferences first, so silently defaulting a
     * missing field would otherwise destroy progress.
     */
    internal val currentSchemaRequiredKeys = setOf(
        "save_schema_version",
        "scene",
        "rep",
        "guard",
        "seleste",
        "gold",
        "hp",
        "inf",
        "f_crown",
        "f_merchants",
        "f_people",
        "f_rose",
        "quest_market",
        "quest_medicine_crate_found",
        "quest_rose_chain",
        "quest_rose_path",
        "quest_houses_stage",
        "quest_houses_path",
        "mem_garrett",
        "mem_seleste",
        "events",
        "mem_thomas",
        "mem_mira",
        "bond_thomas",
        "bond_mira",
        "bond_reynar",
        "social_conflict",
        "social_event",
        "personal_quests",
        "social_event_flags",
        "social_action_flags",
        "location",
        "world_day",
        "world_hour",
        "world_event",
        "battle_enemy",
        "battle_enemy_hp",
        "battle_wins",
        "battle_defeats",
        "battle_reward_day",
        "battle_rewards_today",
        "wounded_until_day",
        "battle_log",
        "experience",
        "hero_level",
        "skill_points",
        "skill_power",
        "skill_guard",
        "encounters",
        "outcome",
        "chosen",
        "dialog_npc",
        "dialog_stage",
        "dialog_trust",
        "journal",
        "items",
        "dungeon_keep",
        "dungeon_crypt",
        "dungeon_archive",
        "expedition_note",
        "weather_seed",
        "crafted_count",
        "council_progress",
        "council_balance",
        "council_report",
        "boss_defeated",
        "world_investigation_flags",
        "companion",
        "expedition_runs",
        "companion_expedition_day",
        "companion_bond",
        "bond_companion_garrett",
        "bond_companion_celeste",
        "bond_companion_mira",
        "bond_companion_thomas",
        "companion_talk_day",
        "companion_moment_report",
        "story_companion_garrett",
        "story_companion_celeste",
        "story_companion_mira",
        "story_companion_thomas",
        "companion_story_report",
        "relation_garrett_celeste",
        "relation_garrett_mira",
        "relation_garrett_thomas",
        "relation_celeste_mira",
        "relation_celeste_thomas",
        "relation_mira_thomas",
        "companion_relation_day",
        "companion_relation_report",
        "companion_team_event_flags",
        "companion_team_event_day",
        "companion_team_event_report",
        "expedition_report",
        "frontier_stage",
        "frontier_route",
        "frontier_threat",
        "frontier_report",
        "a11_army",
        "a11_provisions",
        "a11_treasury",
        "a11_tax_day",
        "a14_upkeep_day",
        "a11_report",
        "a13_prosperity",
        "a13_loyalty",
        "a13_stewards",
        "a13_tax_days",
        "a15_crisis_days",
        "a13_report",
        "a13_income_day"
    )

    private val integerKeys = setOf(
        "save_schema_version", "scene", "rep", "guard", "seleste", "gold", "hp", "inf",
        "companion_expedition_day", "f_crown", "f_merchants", "f_people", "f_rose",
        "quest_market", "quest_rose_chain", "quest_rose_path", "quest_houses_stage",
        "quest_houses_path", "mem_garrett", "mem_seleste", "events", "mem_thomas",
        "mem_mira", "bond_thomas", "bond_mira", "bond_reynar", "social_conflict",
        "personal_quests", "social_event_flags", "social_action_flags", "world_day",
        "world_hour", "battle_enemy_hp", "battle_wins", "battle_defeats", "battle_reward_day",
        "battle_rewards_today", "wounded_until_day", "experience", "hero_level", "skill_points",
        "skill_power", "skill_guard", "encounters", "dialog_stage", "dialog_trust",
        "dungeon_keep", "dungeon_crypt", "dungeon_archive", "weather_seed", "crafted_count",
        "council_progress", "council_balance", "expedition_runs", "companion_bond",
        "bond_companion_garrett", "bond_companion_celeste", "bond_companion_mira", "bond_companion_thomas", "companion_talk_day",
        "story_companion_garrett", "story_companion_celeste", "story_companion_mira", "story_companion_thomas",
        "relation_garrett_celeste", "relation_garrett_mira", "relation_garrett_thomas", "relation_celeste_mira", "relation_celeste_thomas", "relation_mira_thomas", "companion_relation_day", "companion_team_event_flags", "companion_team_event_day",
        "frontier_stage", "world_investigation_flags", "frontier_route", "frontier_threat",
        "a11_army", "a11_provisions", "a11_treasury", "a11_tax_day", "a13_income_day",
        "a14_upkeep_day"
    )

    private val stringKeys = setOf(
        "social_event", "location", "world_event", "battle_enemy", "battle_log", "outcome",
        "dialog_npc", "journal", "items", "expedition_note", "council_report", "companion",
        "expedition_report", "companion_moment_report", "companion_story_report", "companion_relation_report", "companion_team_event_report", "frontier_report", "a11_report", "a13_prosperity", "a13_loyalty",
        "a13_stewards", "a13_tax_days", "a15_crisis_days", "a13_report"
    )

    private val booleanKeys = setOf("chosen", "quest_medicine_crate_found", "boss_defeated")

    /** Apply the same numeric bounds to legacy SharedPreferences reads as to imported saves. */
    fun normalizeInteger(key: String, value: Int, maxSceneIndex: Int): Int =
        boundedFields(maxSceneIndex)[key]?.let(value::coerceIn) ?: value

    private fun boundedFields(maxSceneIndex: Int): Map<String, IntRange> = mapOf(
        "scene" to (0..maxSceneIndex.coerceAtLeast(0)), "rep" to (-100..100), "guard" to (0..100),
        "seleste" to (-100..100), "gold" to (0..1_000_000), "hp" to (0..100),
        // Legacy relationship tracks and counters must be bounded at the manual-save boundary too.
        "bond_thomas" to (-100..100), "bond_mira" to (-100..100), "bond_reynar" to (-100..100),
        "social_conflict" to (0..3), "mem_garrett" to (0..1_000_000),
        "mem_seleste" to (0..1_000_000), "mem_thomas" to (0..1_000_000),
        "mem_mira" to (0..1_000_000), "events" to (0..1_000_000),
        "crafted_count" to (0..1_000_000), "encounters" to (0..1_000_000),
        "inf" to (0..100), "f_crown" to (-100..100), "f_merchants" to (-100..100),
        "f_people" to (-100..100), "f_rose" to (-100..100), "quest_market" to (0..2),
        "quest_rose_chain" to (0..5), "quest_rose_path" to (0..2),
        "quest_houses_stage" to (0..3), "quest_houses_path" to (0..3),
        "personal_quests" to (0..15), "social_event_flags" to (0..255),
        "social_action_flags" to (0..255), "world_day" to (1..1_000_000), "world_hour" to (0..23),
        "battle_enemy_hp" to (0..100_000), "battle_wins" to (0..1_000_000),
        "battle_defeats" to (0..1_000_000), "battle_reward_day" to (1..1_000_000),
        "battle_rewards_today" to (0..4), "wounded_until_day" to (0..1_000_000),
        "experience" to (0..1_000_000), "hero_level" to (1..1_000),
        "skill_points" to (0..100_000), "skill_power" to (0..10), "skill_guard" to (0..10),
        "dialog_stage" to (0..3), "dialog_trust" to (-100..100), "dungeon_keep" to (0..4),
        "dungeon_crypt" to (0..4), "dungeon_archive" to (0..4), "council_progress" to (0..3),
        "council_balance" to (-20..20), "expedition_runs" to (0..1_000_000),
        "companion_bond" to (-100..100), "bond_companion_garrett" to (-100..100),
        "bond_companion_celeste" to (-100..100), "bond_companion_mira" to (-100..100),
        "bond_companion_thomas" to (-100..100), "companion_talk_day" to (0..1_000_000),
        "story_companion_garrett" to (0..3), "story_companion_celeste" to (0..3),
        "story_companion_mira" to (0..3), "story_companion_thomas" to (0..3),
        "relation_garrett_celeste" to (-100..100), "relation_garrett_mira" to (-100..100),
        "relation_garrett_thomas" to (-100..100), "relation_celeste_mira" to (-100..100),
        "relation_celeste_thomas" to (-100..100), "relation_mira_thomas" to (-100..100),
        "companion_relation_day" to (0..1_000_000), "companion_team_event_flags" to (0..63),
        "companion_team_event_day" to (0..1_000_000),
        "frontier_stage" to (0..3), "frontier_route" to (0..3),
        "world_investigation_flags" to (0..15),
        "frontier_threat" to (0..100), "a11_army" to (0..100), "a11_provisions" to (0..100),
        "a11_treasury" to (1..3), "companion_expedition_day" to (0..1_000_000),
        "a11_tax_day" to (0..1_000_000), "a13_income_day" to (0..1_000_000),
        "a14_upkeep_day" to (1..1_000_000)
    )

    /**
     * Validates a primitive-only snapshot and returns a bounded copy. The input map is never
     * mutated. Missing optional fields remain missing so existing in-game defaults still apply.
     */
    fun validateSnapshot(
        source: Map<String, Any?>,
        maxSceneIndex: Int
    ): SaveValidationResult {
        if (source.isEmpty()) return SaveValidationResult.Rejected("Сохранение пустое.")
        val missing = requiredKeys.firstOrNull { key -> !source.containsKey(key) }
        if (missing != null) return SaveValidationResult.Rejected("Сохранение неполное: отсутствует поле $missing.")

        val schemaPresent = source.containsKey("save_schema_version")
        val schemaValue = source["save_schema_version"]
        if (schemaPresent && (schemaValue == null || !isWholeNumber(schemaValue))) {
            return SaveValidationResult.Rejected("Сохранение повреждено (поле save_schema_version).")
        }
        // A current snapshot with its schema marker removed must not silently fall back
        // to the checksum-free legacy path. The two team-scene fields identify schema 11;
        // older genuinely legacy snapshots never carried the full current key set.
        if (!schemaPresent) {
            val currentFingerprint = currentSchemaRequiredKeys - setOf(
                "save_schema_version", "companion_team_event_flags", "companion_team_event_day"
            )
            if (currentFingerprint.all(source::containsKey) ||
                source.containsKey("companion_team_event_flags") || source.containsKey("companion_team_event_day")) {
                return SaveValidationResult.Rejected("Сохранение повреждено: отсутствует версия схемы.")
            }
        }
        // Do not narrow an untrusted Long/Double to Int before validating its range:
        // toInt() wraps values such as 4_294_967_307 into the current schema (11).
        val schemaLong = (schemaValue as? Number)?.toLong() ?: 1L
        if (schemaLong < 1L || schemaLong > Int.MAX_VALUE.toLong()) {
            return SaveValidationResult.Rejected("Версия сохранения не поддерживается.")
        }
        if (schemaLong > CURRENT_SCHEMA.toLong()) {
            return SaveValidationResult.Rejected("Это сохранение создано более новой версией игры.")
        }
        val schema = schemaLong.toInt()
        if (schema >= CURRENT_SCHEMA) {
            val missingCurrent = currentSchemaRequiredKeys.firstOrNull { key -> !source.containsKey(key) }
            if (missingCurrent != null) {
                return SaveValidationResult.Rejected(
                    "Сохранение текущей версии неполное: отсутствует поле $missingCurrent."
                )
            }
        }

        val malformedInteger = integerKeys.firstOrNull { key -> source.containsKey(key) && !isWholeNumber(source[key]) }
        val malformedString = stringKeys.firstOrNull { key -> source.containsKey(key) && source[key] !is String }
        val malformedBoolean = booleanKeys.firstOrNull { key -> source.containsKey(key) && source[key] !is Boolean }
        val malformedDialogue = source.keys.firstOrNull { key ->
            (key.startsWith("dialog_stage_") || key.startsWith("dialog_trust_")) && !isWholeNumber(source[key])
        }
        val unsupported = source.entries.firstOrNull { (_, value) ->
            value != null && value !is String && value !is Number && value !is Boolean
        }?.key
        val malformed = malformedInteger ?: malformedString ?: malformedBoolean ?: malformedDialogue ?: unsupported
        if (malformed != null) {
            return SaveValidationResult.Rejected("Сохранение повреждено (поле $malformed).")
        }

        val bounds = boundedFields(maxSceneIndex)
        val normalized = LinkedHashMap<String, Any?>(source.size + 1)
        source.forEach { (key, value) ->
            when {
                value is Number && bounds.containsKey(key) ->
                    normalized[key] = value.toDouble().coerceIn(
                        bounds.getValue(key).first.toDouble(), bounds.getValue(key).last.toDouble()
                    ).toInt()
                value is Number && key.startsWith("dialog_stage_") ->
                    normalized[key] = value.toDouble().coerceIn(0.0, 3.0).toInt()
                value is Number && key.startsWith("dialog_trust_") ->
                    normalized[key] = value.toDouble().coerceIn(-100.0, 100.0).toInt()
                key == "location" && value is String ->
                    normalized[key] = WorldManager.normalizeLocation(value)
                else -> normalized[key] = value
            }
        }
        // Schema 8 separates companion relationships. A legacy single bond belongs to
        // the companion selected in that save; other companions start at neutral 0.
        if (schema < 8) {
            val selected = normalized["companion"] as? String ?: "Гаррет Морн"
            val legacyBond = (normalized["companion_bond"] as? Number)?.toInt() ?: 0
            val migration = CompanionManager.migrateLegacyBonds(
                selected,
                legacyBond,
                mapOf(
                    "Гаррет Морн" to normalized["bond_companion_garrett"] as? Int,
                    "Селеста" to normalized["bond_companion_celeste"] as? Int,
                    "Мира" to normalized["bond_companion_mira"] as? Int,
                    "Томас" to normalized["bond_companion_thomas"] as? Int
                )
            )
            normalized["bond_companion_garrett"] = migration.getValue("Гаррет Морн")
            normalized["bond_companion_celeste"] = migration.getValue("Селеста")
            normalized["bond_companion_mira"] = migration.getValue("Мира")
            normalized["bond_companion_thomas"] = migration.getValue("Томас")
            normalized.putIfAbsent("companion_talk_day", 0)
        }
        // Schema 9 adds independent three-chapter companion story progress. Older saves start at chapter 0.
        if (schema < 9) {
            normalized.putIfAbsent("story_companion_garrett", 0)
            normalized.putIfAbsent("story_companion_celeste", 0)
            normalized.putIfAbsent("story_companion_mira", 0)
            normalized.putIfAbsent("story_companion_thomas", 0)
        }
        // Schema 10 introduces six symmetric companion-to-companion affinity tracks.
        // Existing campaigns begin at neutral affinity without disturbing hero bonds.
        if (schema < 10) {
            listOf("garrett_celeste", "garrett_mira", "garrett_thomas", "celeste_mira", "celeste_thomas", "mira_thomas")
                .forEach { pair -> normalized.putIfAbsent("relation_$pair", 0) }
            normalized.putIfAbsent("companion_relation_day", 0)
        }
        // Schema 11 adds one-time cooperative scenes for each companion pair.
        // Old campaigns retain all affinity tracks and begin with unplayed team scenes.
        if (schema < 11) {
            normalized.putIfAbsent("companion_team_event_flags", 0)
            normalized.putIfAbsent("companion_team_event_day", 0)
        }
        // A completed day settlement always persists both markers together. Older saves
        // predate this marker, so initialize it to their current day instead of replaying
        // an unbounded backlog of army upkeep and world events on the next time advance.
        val savedWorldDay = (normalized["world_day"] as? Int)?.coerceAtLeast(1) ?: 1
        if (schema < CURRENT_SCHEMA && "a14_upkeep_day" !in source) {
            normalized["a14_upkeep_day"] = savedWorldDay
        } else if (schema >= CURRENT_SCHEMA) {
            val upkeepDay = (normalized["a14_upkeep_day"] as? Int) ?: savedWorldDay
            if (upkeepDay != savedWorldDay) {
                return SaveValidationResult.Rejected(
                    "Сохранение повреждено: день мира не совпадает с отметкой ежедневного расчёта."
                )
            }
        }
        normalized["save_schema_version"] = CURRENT_SCHEMA
        return SaveValidationResult.Accepted(normalized, schema)
    }

    private fun isWholeNumber(value: Any?): Boolean {
        val number = value as? Number ?: return false
        val doubleValue = number.toDouble()
        return doubleValue.isFinite() && doubleValue % 1.0 == 0.0 &&
            doubleValue >= Long.MIN_VALUE.toDouble() && doubleValue <= Long.MAX_VALUE.toDouble()
    }

    /** SHA-256 detects valid-JSON corruption that structural parsing alone cannot detect. */
    fun checksum(payload: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(payload.toByteArray(Charsets.UTF_8))
        return digest.joinToString(separator = "") { byte -> "%02x".format(byte) }
    }

    /** Current-schema snapshots must carry an integrity hash; only pre-hash legacy slots may omit it. */
    fun requiresChecksum(sourceSchema: Int): Boolean = sourceSchema >= CURRENT_SCHEMA

    fun checksumMatches(payload: String, expected: String?): Boolean =
        expected.isNullOrBlank() || checksum(payload).equals(expected, ignoreCase = true)
}
