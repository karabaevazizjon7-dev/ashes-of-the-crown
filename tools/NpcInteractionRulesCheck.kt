package com.ashes.crown

fun main() {
    fun available(name: String, location: String, hour: Int) =
        NpcInteractionManager.evaluate(name, location, hour) is NpcInteractionResult.Available

    // Garrett: present and free at dawn, away at the outpost, then present but busy.
    check(available("Гаррет Морн", "Королевский замок", 6))
    val garrettAway = NpcInteractionManager.evaluate("Гаррет Морн", "Королевский замок", 14)
    check(garrettAway is NpcInteractionResult.Away && garrettAway.npcLocation == "Северная застава")
    val garrettBusy = NpcInteractionManager.evaluate("Гаррет Морн", "Королевский замок", 19)
    check(garrettBusy is NpcInteractionResult.Busy)

    // Celeste and Mira have distinct availability windows and locations.
    check(available("Селеста", "Зимний сад", 9))
    check(NpcInteractionManager.evaluate("Селеста", "Зимний сад", 14) is NpcInteractionResult.Away)
    check(NpcInteractionManager.evaluate("Селеста", "Королевский замок", 14) is NpcInteractionResult.Busy)
    check(available("Мира", "Нижний город", 8))
    check(available("Мира", "Рыночная площадь", 13))
    check(NpcInteractionManager.evaluate("Мира", "Нижний город", 20) is NpcInteractionResult.Busy)

    // Thomas works at night, is free before the shift, and sleeps after it.
    check(NpcInteractionManager.evaluate("Томас", "Порт Веймар", 21) is NpcInteractionResult.Busy)
    check(available("Томас", "Нижний город", 18))
    check(NpcInteractionManager.evaluate("Томас", "Порт Веймар", 10) is NpcInteractionResult.Busy)

    // Legacy port alias is canonicalized; unscheduled Duke Reynar remains castle-only.
    check(available("Томас", "Порт Эстервала", 18).not())
    check(NpcInteractionManager.evaluate("Томас", "Порт Эстервала", 21) is NpcInteractionResult.Busy)
    check(available("Герцог Рейнар", "Королевский замок", 9))
    check(NpcInteractionManager.evaluate("Герцог Рейнар", "Нижний город", 9) is NpcInteractionResult.Away)

    check(!NpcInteractionManager.isAvailable("Гаррет Морн", "Королевский замок", 14))
    check(NpcInteractionManager.message(garrettAway).contains("Северная застава"))
    check(NpcInteractionManager.message(garrettBusy).contains("занят"))

    println("NPC interaction location, timetable, busy-state, legacy alias and unscheduled-story fallback checks passed")
}
