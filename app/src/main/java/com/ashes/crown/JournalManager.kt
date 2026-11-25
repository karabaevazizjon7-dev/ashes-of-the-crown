package com.ashes.crown

/** Pure journal storage rules; newest entries are retained when the log is bounded. */
internal object JournalManager {
    const val MAX_ENTRIES = 500

    fun normalize(entries: List<String>, limit: Int = MAX_ENTRIES): List<String> {
        val safeLimit = limit.coerceAtLeast(0)
        if (safeLimit == 0) return emptyList()
        return entries.asSequence().filter { it.isNotBlank() }.toList().takeLast(safeLimit)
    }

    fun append(entries: List<String>, entry: String, limit: Int = MAX_ENTRIES): List<String> {
        if (entry.isBlank() || limit <= 0) return normalize(entries, limit)
        return normalize(entries + entry, limit)
    }
}
