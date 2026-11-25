package com.ashes.crown

/** Shared responsive rules for wide, full-page collections such as the inventory and relationships. */
object CollectionLayoutRules {
    fun columns(availableWidthDp: Int): Int = if (availableWidthDp >= 560) 2 else 1

    fun rowCount(itemCount: Int, columns: Int): Int {
        if (itemCount <= 0) return 0
        val safeColumns = columns.coerceAtLeast(1)
        return (itemCount + safeColumns - 1) / safeColumns
    }

    fun missingCellsInLastRow(itemCount: Int, columns: Int): Int {
        if (itemCount <= 0) return 0
        val safeColumns = columns.coerceAtLeast(1)
        return (safeColumns - itemCount % safeColumns) % safeColumns
    }
}
