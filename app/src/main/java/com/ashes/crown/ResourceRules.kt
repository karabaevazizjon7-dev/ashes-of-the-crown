package com.ashes.crown

/** Shared resource bounds used by live gameplay and save normalization. */
internal object ResourceRules {
    const val MAX_GOLD = 1_000_000
    const val MAX_INFLUENCE = 100

    fun normalizeGold(value: Int): Int = value.coerceIn(0, MAX_GOLD)

    /** Safe for positive rewards, negative costs and Int boundary inputs. */
    fun addGold(current: Int, delta: Int): Int =
        (current.toLong() + delta.toLong()).coerceIn(0L, MAX_GOLD.toLong()).toInt()

    fun addInfluence(current: Int, delta: Int): Int =
        (current.toLong() + delta.toLong()).coerceIn(0L, MAX_INFLUENCE.toLong()).toInt()
}
