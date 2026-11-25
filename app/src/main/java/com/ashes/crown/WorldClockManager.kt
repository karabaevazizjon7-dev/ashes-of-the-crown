package com.ashes.crown

/** Named time-of-day phase shared by the clock UI and world-time messages. */
internal enum class WorldTimePhase(val label: String) {
    NIGHT("ночь"), MORNING("утро"), DAY("день"), EVENING("вечер")
}

/** Immutable plan for advancing the campaign clock; it performs no game-state mutation. */
internal data class WorldClockAdvance(
    val targetDay: Int,
    val targetHour: Int,
    val elapsedHours: Int,
    val pendingUpkeepDays: Int,
    val phase: WorldTimePhase,
    val timeMessage: String
)

/** Pure clock arithmetic extracted from Compose so time progression can be tested independently. */
internal object WorldClockManager {
    fun advance(currentDay: Int, currentHour: Int, requestedHours: Int, lastUpkeepDay: Int): WorldClockAdvance {
        val safeDay = currentDay.coerceAtLeast(1)
        val safeHour = currentHour.coerceIn(0, 23)
        val elapsed = requestedHours.coerceAtLeast(0)
        val totalHours = safeHour.toLong() + elapsed.toLong()
        val targetDay = (safeDay.toLong() + totalHours / 24L)
            .coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        val targetHour = (totalHours % 24L).toInt()
        val pendingDays = (targetDay.toLong() - lastUpkeepDay.coerceAtLeast(0).toLong())
            .coerceIn(0L, Int.MAX_VALUE.toLong()).toInt()
        val phase = phaseAt(targetHour)
        return WorldClockAdvance(
            targetDay = targetDay,
            targetHour = targetHour,
            elapsedHours = elapsed,
            pendingUpkeepDays = pendingDays,
            phase = phase,
            timeMessage = transitionMessage(safeHour, targetHour, phase)
        )
    }

    fun phaseAt(hour: Int): WorldTimePhase = when (hour.coerceIn(0, 23)) {
        in 5..10 -> WorldTimePhase.MORNING
        in 11..16 -> WorldTimePhase.DAY
        in 17..20 -> WorldTimePhase.EVENING
        else -> WorldTimePhase.NIGHT
    }

    private fun transitionMessage(beforeHour: Int, targetHour: Int, phase: WorldTimePhase): String = when {
        beforeHour < 18 && targetHour >= 18 ->
            "На улицах зажигают фонари. Стража усиливает патрули, а торговцы закрывают лавки."
        beforeHour < 6 && targetHour >= 6 ->
            "Колокола возвещают рассвет. Рынок постепенно наполняется людьми."
        targetHour in 22..23 || targetHour in 0..4 ->
            "Ночные улицы почти пусты. В переулках слышны шаги, а у ворот проверяют путников."
        else -> "В Эстервaле сейчас ${phase.label}. Горожане заняты повседневными делами."
    }
}
