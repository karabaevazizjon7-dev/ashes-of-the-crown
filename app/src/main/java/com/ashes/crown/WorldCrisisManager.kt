package com.ashes.crown

/** Severity of a province crisis, derived from the campaign's existing crisis-day counter. */
internal enum class ProvinceCrisisStage { STABLE, UNREST, ESCALATING, REVOLT }

internal data class ProvinceCrisisStatus(
    val index: Int,
    val name: String,
    val stage: ProvinceCrisisStage,
    val days: Int,
    val loyalty: Int,
    val prosperity: Int,
    val title: String,
    val description: String,
    val recommendedAction: String,
    val urgency: Int
)

internal data class WorldCrisisReport(
    val provinces: List<ProvinceCrisisStatus>,
    val mostUrgent: ProvinceCrisisStatus?,
    val summary: String,
    val activeCount: Int,
    val revoltCount: Int
)

/**
 * Turns the existing provincial crisis counters into a readable multi-stage crisis model.
 * It is intentionally derived-only: no new save fields, timers or duplicate state are introduced.
 */
internal object WorldCrisisManager {
    fun evaluate(
        names: List<String>,
        loyalty: List<Int>,
        prosperity: List<Int>,
        crisisDays: List<Int>
    ): WorldCrisisReport {
        val count = minOf(names.size, loyalty.size, prosperity.size, crisisDays.size)
        val statuses = (0 until count).map { index ->
            val days = crisisDays[index].coerceIn(0, 3)
            val safeLoyalty = loyalty[index].coerceIn(0, 100)
            val safeProsperity = prosperity[index].coerceIn(0, 100)
            val stage = when (days) {
                0 -> ProvinceCrisisStage.STABLE
                1 -> ProvinceCrisisStage.UNREST
                2 -> ProvinceCrisisStage.ESCALATING
                else -> ProvinceCrisisStage.REVOLT
            }
            val title: String
            val description: String
            val action: String
            val urgency: Int
            when (stage) {
                ProvinceCrisisStage.STABLE -> {
                    title = "Стабильность"
                    description = "Местные общины сохраняют спокойствие."
                    action = "Поддерживай процветание и не злоупотребляй налогами."
                    urgency = 0
                }
                ProvinceCrisisStage.UNREST -> {
                    title = "Недовольство"
                    description = "Первые признаки неповиновения: жители теряют доверие к управлению."
                    action = "Помощь или переговоры могут остановить кризис до эскалации."
                    urgency = 1
                }
                ProvinceCrisisStage.ESCALATING -> {
                    title = "Нарастающий кризис"
                    description = "Общины всё чаще отказываются выполнять распоряжения; провинция близка к открытому мятежу."
                    action = "Не откладывай урегулирование: доставь помощь или начни переговоры."
                    urgency = 2
                }
                ProvinceCrisisStage.REVOLT -> {
                    title = "Восстание"
                    description = "Власть короны фактически отвергнута. Налоговые поступления остановлены, хозяйство несёт потери."
                    action = "Сначала восстанови верность жителей переговорами или помощью; сбор налогов недоступен."
                    urgency = 3
                }
            }
            ProvinceCrisisStatus(index, names[index], stage, days, safeLoyalty, safeProsperity,
                title, description, action, urgency)
        }
        val active = statuses.filter { it.stage != ProvinceCrisisStage.STABLE }
        val urgent = active.maxWithOrNull(compareBy<ProvinceCrisisStatus> { it.urgency }
            .thenBy { -it.loyalty }.thenBy { -it.prosperity }.thenBy { -it.index })
        val revoltCount = statuses.count { it.stage == ProvinceCrisisStage.REVOLT }
        val summary = when {
            active.isEmpty() -> "Все провинции спокойны; активных кризисов нет."
            revoltCount > 0 -> "Восстание охватило ${revoltCount} пров. Активные кризисы: ${active.size}. Наиболее опасная: ${urgent?.name}."
            else -> "Недовольство затронуло ${active.size} пров. Наиболее срочная: ${urgent?.name} (${urgent?.title?.lowercase()})."
        }
        return WorldCrisisReport(statuses, urgent, summary, active.size, revoltCount)
    }
}
