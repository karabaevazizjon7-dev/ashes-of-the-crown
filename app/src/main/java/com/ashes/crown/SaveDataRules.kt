package com.ashes.crown

/** Pure helpers for reading legacy/corrupted delimited save fields safely. */
internal object SaveDataRules {
    /**
     * Parses a fixed-size integer list without shifting values when one token is
     * malformed. Missing or invalid positions fall back to that position's default.
     */
    fun parseBoundedIntList(
        raw: String?,
        defaults: List<Int>,
        minimum: Int,
        maximum: Int
    ): List<Int> {
        require(minimum <= maximum) { "minimum must not exceed maximum" }
        val tokens = raw?.split(',').orEmpty()
        return defaults.mapIndexed { index, defaultValue ->
            val fallback = defaultValue.coerceIn(minimum, maximum)
            tokens.getOrNull(index)
                ?.trim()
                ?.toIntOrNull()
                ?.coerceIn(minimum, maximum)
                ?: fallback
        }
    }

    /** SharedPreferences removes a key when putString receives null; encode nullable UI text explicitly. */
    fun encodeOptionalText(value: String?): String = value.orEmpty()

    /** Empty serialized optional text restores to the original absent/null UI state. */
    fun decodeOptionalText(value: String?): String? = value?.takeIf { it.isNotBlank() }

    /** Daily markers must never point into the future relative to the loaded world. */
    fun normalizeDayMarker(value: Int, currentDay: Int, allowZero: Boolean = true): Int {
        val lowerBound = if (allowZero) 0 else 1
        return value.coerceIn(lowerBound, currentDay.coerceAtLeast(lowerBound))
    }
}
