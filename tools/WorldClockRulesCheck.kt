package com.ashes.crown

/** Regression checks for clock arithmetic and time-of-day messaging. */
object WorldClockRulesCheck {
    @JvmStatic fun main(args: Array<String>) {
        val noAdvance = WorldClockManager.advance(7, 9, 0, 7)
        check(noAdvance.targetDay == 7 && noAdvance.targetHour == 9)
        check(noAdvance.pendingUpkeepDays == 0 && noAdvance.elapsedHours == 0)
        check(noAdvance.phase == WorldTimePhase.MORNING)

        val withinDay = WorldClockManager.advance(7, 9, 5, 7)
        check(withinDay.targetDay == 7 && withinDay.targetHour == 14)
        check(withinDay.phase == WorldTimePhase.DAY)
        check(withinDay.timeMessage.contains("день"))

        val oneDay = WorldClockManager.advance(7, 9, 15, 7)
        check(oneDay.targetDay == 8 && oneDay.targetHour == 0)
        check(oneDay.pendingUpkeepDays == 1)
        check(oneDay.timeMessage.contains("Ночные улицы"))

        val severalDays = WorldClockManager.advance(7, 22, 74, 5)
        check(severalDays.targetDay == 11 && severalDays.targetHour == 0)
        check(severalDays.pendingUpkeepDays == 6)
        check(severalDays.elapsedHours == 74)

        val overdueUpkeep = WorldClockManager.advance(12, 12, 0, 9)
        check(overdueUpkeep.targetDay == 12 && overdueUpkeep.pendingUpkeepDays == 3)

        val negativeHours = WorldClockManager.advance(4, 8, -50, 4)
        check(negativeHours.targetDay == 4 && negativeHours.targetHour == 8)
        check(negativeHours.elapsedHours == 0)

        val sunrise = WorldClockManager.advance(3, 5, 1, 3)
        check(sunrise.targetHour == 6 && sunrise.timeMessage.contains("рассвет"))
        val dusk = WorldClockManager.advance(3, 17, 1, 3)
        check(dusk.targetHour == 18 && dusk.timeMessage.contains("фонари"))

        val corruptClock = WorldClockManager.advance(0, 99, 1, -10)
        check(corruptClock.targetDay == 2 && corruptClock.targetHour == 0)
        check(corruptClock.pendingUpkeepDays == 2)

        val huge = WorldClockManager.advance(Int.MAX_VALUE, 23, Int.MAX_VALUE, 0)
        check(huge.targetDay == Int.MAX_VALUE)
        check(huge.targetHour in 0..23)
        check(huge.pendingUpkeepDays == Int.MAX_VALUE)

        for (hour in -48..72) {
            val phase = WorldClockManager.phaseAt(hour)
            val normalized = hour.coerceIn(0, 23)
            check(phase == when (normalized) {
                in 5..10 -> WorldTimePhase.MORNING
                in 11..16 -> WorldTimePhase.DAY
                in 17..20 -> WorldTimePhase.EVENING
                else -> WorldTimePhase.NIGHT
            })
        }
        println("WorldClockManager: hour normalization, day rollover, multi-day upkeep planning, phase boundaries and overflow checks passed")
    }
}
