package com.ashes.crown

import android.content.SharedPreferences
import org.json.JSONObject

/** Outcome of an explicit manual-slot operation. */
internal sealed class SaveSlotResult {
    data class Saved(val slot: Int) : SaveSlotResult()
    data class Loaded(val slot: Int, val sourceSchema: Int) : SaveSlotResult()
    data class Rejected(val message: String) : SaveSlotResult()
    data object Missing : SaveSlotResult()
}

/**
 * Android storage adapter for the three manual campaign slots.
 *
 * SaveManager owns the pure validation rules. This class owns only the Android boundary:
 * SharedPreferences snapshots, JSON transport, slot metadata and the final preference commit.
 * It validates a complete detached payload before clearing or replacing active campaign data.
 */
internal class SaveSlotStore(
    private val preferences: SharedPreferences,
    private val maxSceneIndex: Int
) {
    fun saveSlot(slot: Int, label: String): SaveSlotResult {
        if (slot !in SLOT_RANGE) return SaveSlotResult.Rejected("Недопустимый номер слота.")

        val snapshotValues = LinkedHashMap<String, Any?>()
        preferences.all.forEach { (key, value) ->
            if (SaveSlotRules.includeInCampaignSnapshot(key) && value != null) {
                snapshotValues[key] = value
            }
        }
        val normalizedValues = when (val validation = SaveManager.validateSnapshot(snapshotValues, maxSceneIndex)) {
            is SaveValidationResult.Rejected -> return SaveSlotResult.Rejected(
                "Нельзя сохранить некорректное состояние: ${validation.reason}"
            )
            is SaveValidationResult.Accepted -> validation.values
        }

        val payload = try {
            JSONObject().apply {
                normalizedValues.forEach { (key, value) -> put(key, value) }
            }.toString()
        } catch (_: Exception) {
            return SaveSlotResult.Rejected("Не удалось подготовить данные сохранения.")
        }

        val oldSlotValues = slotValues(slot)
        val committed = preferences.edit()
            .putString(slotKey(slot, "data"), payload)
            .putString(slotKey(slot, "checksum"), SaveManager.checksum(payload))
            .putString(slotKey(slot, "label"), label)
            .commit()
        if (!committed) {
            restoreSlotValues(oldSlotValues)
            return SaveSlotResult.Rejected("Не удалось записать сохранение на устройство.")
        }
        return SaveSlotResult.Saved(slot)
    }

    fun loadSlot(slot: Int): SaveSlotResult {
        if (slot !in SLOT_RANGE) return SaveSlotResult.Rejected("Недопустимый номер слота.")
        val raw = preferences.getString(slotKey(slot, "data"), null) ?: return SaveSlotResult.Missing
        val expectedChecksum = preferences.getString(slotKey(slot, "checksum"), null)

        val parsed = try {
            JSONObject(raw)
        } catch (_: Exception) {
            return SaveSlotResult.Rejected("Сохранение повреждено. Текущий прогресс не изменён.")
        }
        val rawValues = LinkedHashMap<String, Any?>(parsed.length())
        val keys = parsed.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = parsed.opt(key)
            rawValues[key] = if (value == JSONObject.NULL) null else value
        }

        val validation = SaveManager.validateSnapshot(rawValues, maxSceneIndex)
        val accepted = when (validation) {
            is SaveValidationResult.Accepted -> validation
            is SaveValidationResult.Rejected -> return SaveSlotResult.Rejected(
                validation.reason + " Текущий прогресс не изменён."
            )
        }

        // A missing hash is tolerated only for legacy schemas created before the
        // current slot-integrity contract. Every current-schema slot is hashed.
        if (expectedChecksum.isNullOrBlank()) {
            if (SaveManager.requiresChecksum(accepted.sourceSchema)) {
                return SaveSlotResult.Rejected(
                    "У сохранения текущей версии отсутствует контрольная сумма. Текущий прогресс не изменён."
                )
            }
        } else if (!SaveManager.checksumMatches(raw, expectedChecksum)) {
            return SaveSlotResult.Rejected(
                "Контрольная сумма сохранения не совпадает. Текущий прогресс не изменён."
            )
        }

        // Keep a rollback image in case the synchronous disk commit fails.
        val previousPreferences = preferences.all.toMap()
        val editor = preferences.edit().clear()
        (previousPreferences[MUSIC_KEY] as? Boolean)?.let { editor.putBoolean(MUSIC_KEY, it) }
        editor.putInt("save_schema_version", SaveManager.CURRENT_SCHEMA)
        accepted.values.forEach { (key, value) ->
            if (!SaveSlotRules.restoreIntoCampaign(key)) return@forEach
            putValue(editor, key, value)
        }
        SLOT_RANGE.forEach { savedSlot ->
            listOf("data", "checksum", "label").forEach { field ->
                val key = slotKey(savedSlot, field)
                (previousPreferences[key] as? String)?.let { editor.putString(key, it) }
            }
        }

        if (!editor.commit()) {
            restoreAll(previousPreferences)
            return SaveSlotResult.Rejected("Не удалось применить сохранение. Предыдущие данные восстановлены.")
        }
        return SaveSlotResult.Loaded(slot, accepted.sourceSchema)
    }

    private fun putValue(editor: SharedPreferences.Editor, key: String, value: Any?) {
        when (value) {
            null -> editor.remove(key)
            is Boolean -> editor.putBoolean(key, value)
            is Int -> editor.putInt(key, value)
            is Long -> editor.putLong(key, value)
            is Float -> editor.putFloat(key, value)
            is Double -> editor.putFloat(key, value.toFloat())
            is Number -> editor.putLong(key, value.toLong())
            is String -> editor.putString(key, value)
        }
    }

    private fun slotValues(slot: Int): Map<String, Any?> = listOf("data", "checksum", "label")
        .associate { field -> slotKey(slot, field) to preferences.all[slotKey(slot, field)] }

    private fun restoreSlotValues(values: Map<String, Any?>) {
        val editor = preferences.edit()
        values.forEach { (key, value) ->
            when (value) {
                null -> editor.remove(key)
                is String -> editor.putString(key, value)
                else -> Unit
            }
        }
        editor.commit()
    }

    private fun restoreAll(values: Map<String, *>) {
        val editor = preferences.edit().clear()
        values.forEach { (key, value) ->
            when (value) {
                is String -> editor.putString(key, value)
                is Boolean -> editor.putBoolean(key, value)
                is Int -> editor.putInt(key, value)
                is Long -> editor.putLong(key, value)
                is Float -> editor.putFloat(key, value)
                is Set<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    editor.putStringSet(key, value.filterIsInstance<String>().toSet())
                }
            }
        }
        editor.commit()
    }

    private fun slotKey(slot: Int, field: String) = "${SLOT_PREFIX}${slot}_$field"

    private companion object {
        const val SLOT_PREFIX = SaveSlotRules.SLOT_PREFIX
        const val MUSIC_KEY = SaveSlotRules.MUSIC_KEY
        val SLOT_RANGE = 1..3
    }
}
