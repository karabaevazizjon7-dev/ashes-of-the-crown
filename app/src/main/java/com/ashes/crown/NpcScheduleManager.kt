package com.ashes.crown

/** One time slice in an NPC's daily route. End hour is exclusive; ranges may cross midnight. */
internal data class NpcScheduleEntry(
    val npcName: String,
    val location: String,
    val startHour: Int,
    val endHourExclusive: Int,
    val activity: String,
    val unavailableActivity: String,
    val availableForConversation: Boolean = true
) {
    fun isActive(hour: Int): Boolean {
        val normalized = Math.floorMod(hour, 24)
        return if (startHour < endHourExclusive) normalized in startHour until endHourExclusive
        else normalized >= startHour || normalized < endHourExclusive
    }
}

internal data class NpcCurrentStatus(
    val npcName: String,
    val location: String,
    val activity: String,
    val availableForConversation: Boolean
)

internal data class NpcScheduleReport(
    val location: String,
    val hour: Int,
    val availableNpcs: List<String>,
    val description: String,
    val presentNpcs: List<String> = emptyList()
)

/**
 * Deterministic daily itineraries for named characters. Each character has exactly
 * one active timetable slice at every hour; locations are derived from campaign time,
 * so no new save fields or migration are needed.
 */
internal object NpcScheduleManager {
    private val schedules = listOf(
        // Garrett patrols the castle in the morning, checks the northern outpost by day,
        // then returns to the castle for the evening and night watch.
        NpcScheduleEntry("Гаррет Морн", "Королевский замок", 6, 12,
            "Гаррет Морн проверяет караулы у оружейной башни и готов обсудить дела двора.",
            "Гаррет отдыхает между сменами в казармах замка."),
        NpcScheduleEntry("Гаррет Морн", "Северная застава", 12, 18,
            "Гаррет инспектирует северную заставу и проверяет донесения пограничников.",
            "Гаррет занят проверкой укреплений и не может отвлечься на разговор.", false),
        NpcScheduleEntry("Гаррет Морн", "Королевский замок", 18, 6,
            "Гаррет вернулся в замок и несёт вечерний караул.",
            "Гаррет на ночном обходе стен. У караульной башни остался младший сержант.", false),

        // Celeste moves between the garden and the castle's audience rooms.
        NpcScheduleEntry("Селеста", "Зимний сад", 8, 12,
            "Селеста гуляет у фонтана и готова к беседе.",
            "В саду тихо; Селеста сейчас в другой части замка.", true),
        NpcScheduleEntry("Селеста", "Королевский замок", 12, 18,
            "Селеста разбирает письма и принимает посетителей в замке.",
            "Селеста занята придворными обязанностями и просит прийти позже.", false),
        NpcScheduleEntry("Селеста", "Зимний сад", 18, 22,
            "Перед вечером Селеста возвращается в зимний сад и может поговорить.",
            "Селеста ненадолго вышла из сада.", true),
        NpcScheduleEntry("Селеста", "Королевский замок", 22, 8,
            "Селеста отдыхает в своих покоях.",
            "После позднего часа Селеста недоступна для беседы.", false),

        // Mira follows the market's opening, busiest hours and closing routine.
        NpcScheduleEntry("Мира", "Нижний город", 7, 12,
            "Мира открыла лавку в Нижнем городе и готова обсуждать поставки.",
            "Лавка Миры ещё закрыта.", true),
        NpcScheduleEntry("Мира", "Рыночная площадь", 12, 19,
            "Мира проверяет торговые ряды на Рыночной площади и может поговорить.",
            "Мира занята расчётами с торговцами.", true),
        NpcScheduleEntry("Мира", "Нижний город", 19, 7,
            "Мира вернулась в Нижний город после закрытия рынка.",
            "Мира отдыхает после долгого торгового дня.", false),

        // Thomas works nights at the port, prepares for his shift in the Lower City,
        // and sleeps through the day after finishing work.
        NpcScheduleEntry("Томас", "Порт Веймар", 20, 5,
            "Томас работает ночную смену. У причалов можно заметить подозрительные грузы.",
            "Томас занят разгрузкой и не может надолго отойти от причала.", false),
        NpcScheduleEntry("Томас", "Порт Веймар", 5, 17,
            "На причалах идёт дневная работа; Томас отдыхает после ночной смены.",
            "Томас спит после ночной смены и не принимает посетителей.", false),
        NpcScheduleEntry("Томас", "Нижний город", 17, 20,
            "Томас зашёл в Нижний город перед ночной сменой и готов поделиться портовыми слухами.",
            "Томас уже отправился к причалам.", true)
    )

    fun report(location: String, hour: Int): NpcScheduleReport {
        val canonical = WorldManager.canonicalName(location)
        val normalizedHour = Math.floorMod(hour, 24)
        val present = schedules.filter { it.location == canonical && it.isActive(normalizedHour) }
        val available = present.filter { it.availableForConversation }.map { it.npcName }.distinct()
        val description = when {
            present.isNotEmpty() -> present.joinToString(" ") {
                if (it.availableForConversation) it.activity else it.unavailableActivity
            }
            normalizedHour in 20..23 || normalizedHour in 0..4 ->
                "У дороги мелькает фигура в плаще. Возможно, это тайный связной."
            else -> "Здесь встречаются путники и сборщики пошлин."
        }
        return NpcScheduleReport(canonical, normalizedHour, available, description,
            present.map { it.npcName }.distinct())
    }

    /** Returns one current, time-derived location/activity record for every named NPC. */
    fun roster(hour: Int): List<NpcCurrentStatus> {
        val normalizedHour = Math.floorMod(hour, 24)
        return schedules.groupBy { it.npcName }.mapNotNull { (name, entries) ->
            entries.firstOrNull { it.isActive(normalizedHour) }?.let { entry ->
                NpcCurrentStatus(
                    npcName = name,
                    location = entry.location,
                    activity = if (entry.availableForConversation) entry.activity else entry.unavailableActivity,
                    availableForConversation = entry.availableForConversation
                )
            }
        }.sortedBy { it.npcName }
    }

    /** Number of whole hours until at least one named NPC changes timetable slice. */
    fun hoursUntilNextChange(hour: Int): Int {
        val current = roster(hour)
        for (offset in 1..24) {
            if (roster(hour + offset) != current) return offset
        }
        return 24
    }

    /** Flattened schedule for diagnostics and tests. */
    fun catalogue(): List<NpcScheduleEntry> = schedules.toList()
}
