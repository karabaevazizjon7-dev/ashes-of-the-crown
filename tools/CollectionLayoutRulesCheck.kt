package com.ashes.crown

fun main() {
    check(CollectionLayoutRules.columns(320) == 1)
    check(CollectionLayoutRules.columns(559) == 1)
    check(CollectionLayoutRules.columns(560) == 2)
    check(CollectionLayoutRules.columns(900) == 2)
    for (columns in 1..2) {
        for (items in 0..100) {
            val rows = CollectionLayoutRules.rowCount(items, columns)
            val missing = CollectionLayoutRules.missingCellsInLastRow(items, columns)
            check(rows == if (items == 0) 0 else (items + columns - 1) / columns)
            check(missing in 0 until columns)
            if (items > 0) check((rows - 1) * columns + (columns - missing) == items)
        }
    }
    println("CollectionLayoutRulesCheck: PASS (202 boundary/layout cases)")
}
