package com.ashes.crown

/** Inputs for a once-per-world-day event. Lists are copied by the caller. */
internal data class WorldEventContext(
    val day: Int,
    val gold: Int,
    val influence: Int,
    val guard: Int,
    val crownReputation: Int,
    val merchantReputation: Int,
    val peopleReputation: Int,
    val roseReputation: Int,
    val provinceLoyalty: List<Int>,
    val provinceProsperity: List<Int>,
    val provinceCrisisDays: List<Int>
)

internal enum class WorldEventKind {
    TRADE_CARAVAN,
    ROYAL_PATROL,
    MARKET_FAIR,
    ROSE_RUMOR,
    PROVINCIAL_UNREST,
    QUIET_DAY
}

/** A deterministic event and its bounded, explicit effects. */
internal data class WorldEventResult(
    val kind: WorldEventKind,
    val title: String,
    val description: String,
    val goldDelta: Int = 0,
    val influenceDelta: Int = 0,
    val guardDelta: Int = 0,
    val crownReputationDelta: Int = 0,
    val merchantReputationDelta: Int = 0,
    val peopleReputationDelta: Int = 0,
    val roseReputationDelta: Int = 0,
    val provinceIndex: Int? = null,
    val provinceLoyaltyDelta: Int = 0,
    val provinceProsperityDelta: Int = 0
)

/**
 * Deterministic daily world news. It does not persist its own state: the existing
 * daily-upkeep marker guarantees that each crossed campaign day is processed once.
 */
internal object WorldEventsManager {
    fun resolve(context: WorldEventContext): WorldEventResult {
        val provinceCount = minOf(
            context.provinceLoyalty.size,
            context.provinceProsperity.size,
            context.provinceCrisisDays.size
        )
        val unrestIndex = (0 until provinceCount)
            .filter { context.provinceCrisisDays[it] > 0 }
            .maxByOrNull { context.provinceCrisisDays[it] }

        val candidates = buildList {
            add(WorldEventKind.TRADE_CARAVAN)
            add(WorldEventKind.ROYAL_PATROL)
            add(WorldEventKind.MARKET_FAIR)
            add(WorldEventKind.ROSE_RUMOR)
            if (unrestIndex != null) add(WorldEventKind.PROVINCIAL_UNREST)
            add(WorldEventKind.QUIET_DAY)
        }
        val seed = context.day.toLong() * 73_856_093L +
            context.crownReputation.toLong() * 31L +
            context.merchantReputation.toLong() * 37L +
            context.peopleReputation.toLong() * 41L +
            context.roseReputation.toLong() * 43L +
            context.gold.toLong() * 17L + provinceCount.toLong() * 13L
        val selected = candidates[Math.floorMod(seed, candidates.size.toLong()).toInt()]

        return when (selected) {
            WorldEventKind.TRADE_CARAVAN -> WorldEventResult(
                kind = selected,
                title = "Торговый караван",
                description = "В Эстерваль прибыл караван с южных трактов. Купцы оживили рынок; казна получила 2 золота, доверие гильдий немного выросло.",
                goldDelta = 2,
                merchantReputationDelta = 1
            )
            WorldEventKind.ROYAL_PATROL -> WorldEventResult(
                kind = selected,
                title = "Королевский дозор",
                description = "Капитаны усилили патрули на дорогах. Дисциплина гвардии укрепилась, двор оценил порядок.",
                guardDelta = 1,
                crownReputationDelta = 1
            )
            WorldEventKind.MARKET_FAIR -> {
                val provinceIndex = (0 until provinceCount)
                    .minByOrNull { context.provinceProsperity[it] }
                WorldEventResult(
                    kind = selected,
                    title = "Ярмарочный день",
                    description = "На городской площади прошла ярмарка. Торговля оживилась, а жители получили повод для праздника.",
                    peopleReputationDelta = 1,
                    provinceIndex = provinceIndex,
                    provinceProsperityDelta = if (provinceIndex == null) 0 else 1
                )
            }
            WorldEventKind.ROSE_RUMOR -> WorldEventResult(
                kind = selected,
                title = "Шёпот Чёрной розы",
                description = "По трактирам разошёлся слух о незнакомце с чёрной печатью. Следы тайной организации вновь всплыли в разговорах.",
                influenceDelta = 1,
                roseReputationDelta = 1
            )
            WorldEventKind.PROVINCIAL_UNREST -> WorldEventResult(
                kind = selected,
                title = "Недовольство в провинции",
                description = "Из-за затянувшегося кризиса в одной из провинций усилилось недовольство. Верность местных жителей снизилась.",
                provinceIndex = unrestIndex,
                provinceLoyaltyDelta = -1
            )
            WorldEventKind.QUIET_DAY -> WorldEventResult(
                kind = selected,
                title = "Спокойный день",
                description = "В королевстве не произошло заметных потрясений. Торговля и повседневная жизнь шли своим чередом."
            )
        }
    }
}
