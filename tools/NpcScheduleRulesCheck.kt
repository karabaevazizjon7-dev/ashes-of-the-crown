package com.ashes.crown

fun main() {
    val catalogue = NpcScheduleManager.catalogue()
    check(catalogue.size == 13) { "Expected 13 timetable slices, got ${catalogue.size}" }
    check(catalogue.map { it.npcName }.toSet().size == 4)

    // Each character must occupy exactly one location at every hour of the day.
    for (hour in -48..72) {
        val roster = NpcScheduleManager.roster(hour)
        check(roster.size == 4) { "Missing NPC status at hour $hour: $roster" }
        check(roster.map { it.npcName }.toSet().size == 4)
        check(roster.all { it.location.isNotBlank() && it.activity.isNotBlank() })
    }

    check(NpcScheduleManager.report("Королевский замок", 6).availableNpcs == listOf("Гаррет Морн"))
    check(NpcScheduleManager.report("Северная застава", 14).presentNpcs == listOf("Гаррет Морн"))
    check(NpcScheduleManager.report("Северная застава", 14).availableNpcs.isEmpty())
    check(NpcScheduleManager.report("Королевский замок", 19).presentNpcs == listOf("Гаррет Морн"))
    check(NpcScheduleManager.report("Зимний сад", 9).availableNpcs == listOf("Селеста"))
    check(NpcScheduleManager.report("Королевский замок", 14).presentNpcs == listOf("Селеста"))
    check(NpcScheduleManager.report("Рыночная площадь", 13).availableNpcs == listOf("Мира"))
    check(NpcScheduleManager.report("Нижний город", 8).availableNpcs == listOf("Мира"))
    check(NpcScheduleManager.report("Порт Веймар", 21).presentNpcs == listOf("Томас"))
    check(NpcScheduleManager.report("Порт Веймар", 10).presentNpcs == listOf("Томас"))
    check(NpcScheduleManager.report("Нижний город", 18).availableNpcs == listOf("Томас"))
    check(NpcScheduleManager.report("Порт Эстервала", 2).location == "Порт Веймар")
    check(NpcScheduleManager.report("Лесной тракт", 22).description.contains("тайный связной"))
    check(NpcScheduleManager.report("Лесной тракт", 12).description.contains("путники"))

    // Every schedule boundary must be detected within 24 hours, including midnight wrap.
    for (hour in 0..23) {
        val untilChange = NpcScheduleManager.hoursUntilNextChange(hour)
        check(untilChange in 1..24)
        check(NpcScheduleManager.roster(hour) != NpcScheduleManager.roster(hour + untilChange))
    }
    check(NpcScheduleManager.roster(2) == NpcScheduleManager.roster(26))
    check(NpcScheduleManager.roster(-1) == NpcScheduleManager.roster(23))

    println("NPC itinerary coverage, four-character roster, location changes, conversation availability, midnight wrap and next-transition checks passed")
}
