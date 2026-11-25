package com.ashes.crown

/** A read-only view over the four faction reputation tracks already stored by the game. */
internal data class FactionStanding(
    val id: String,
    val displayName: String,
    val score: Int,
    val rank: String,
    val description: String
)

internal data class FactionSnapshot(val standings: List<FactionStanding>) {
    fun standing(id: String): FactionStanding? = standings.firstOrNull { it.id == id }
}

/** Pure normalization and presentation rules; persistence keys remain owned by MainActivity. */
internal object FactionManager {
    const val MIN_REPUTATION = -100
    const val MAX_REPUTATION = 100

    private data class FactionDefinition(val id: String, val name: String, val hostile: String, val neutral: String, val friendly: String, val revered: String)
    private val definitions = listOf(
        FactionDefinition("crown", "Корона", "Подозрение", "Осторожный нейтралитет", "Доверие двора", "Опора короны"),
        FactionDefinition("merchants", "Торговые гильдии", "Недоверие купцов", "Деловой нейтралитет", "Уважаемый партнёр", "Покровитель торговли"),
        FactionDefinition("people", "Народ", "Народное недовольство", "Настороженность", "Народная поддержка", "Народный герой"),
        FactionDefinition("rose", "Чёрная роза", "Враг организации", "Неопределённое положение", "Полезный союзник", "Доверенное лицо")
    )

    fun normalize(value: Int): Int = value.coerceIn(MIN_REPUTATION, MAX_REPUTATION)

    fun applyDelta(current: Int, delta: Int): Int =
        (current.toLong() + delta.toLong()).coerceIn(MIN_REPUTATION.toLong(), MAX_REPUTATION.toLong()).toInt()

    fun rank(id: String, score: Int): String {
        val definition = definitions.firstOrNull { it.id == id } ?: return "Неизвестная фракция"
        val value = normalize(score)
        return when {
            value <= -50 -> definition.hostile
            value < 20 -> definition.neutral
            value < 60 -> definition.friendly
            else -> definition.revered
        }
    }

    fun snapshot(crown: Int, merchants: Int, people: Int, rose: Int): FactionSnapshot {
        val values = listOf(crown, merchants, people, rose)
        return FactionSnapshot(definitions.mapIndexed { index, definition ->
            val score = normalize(values[index])
            FactionStanding(definition.id, definition.name, score, rank(definition.id, score),
                when {
                    score <= -50 -> "Отношения серьёзно испорчены. Решения могут вызвать сопротивление."
                    score < 20 -> "Фракция пока не определилась с отношением к герою."
                    score < 60 -> "Фракция в целом расположена к герою."
                    else -> "Герой пользуется значительным доверием этой фракции."
                })
        })
    }
}
