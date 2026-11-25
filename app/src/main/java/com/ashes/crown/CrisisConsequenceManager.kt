package com.ashes.crown

/** A one-time consequence emitted when a province crosses into a new crisis stage. */
internal data class CrisisTransition(
    val provinceIndex: Int,
    val provinceName: String,
    val from: ProvinceCrisisStage,
    val to: ProvinceCrisisStage,
    val crownDelta: Int,
    val merchantDelta: Int,
    val peopleDelta: Int,
    val roseDelta: Int,
    val neighboringLoyaltyLoss: Int,
    val message: String
)

/**
 * Converts crisis-stage transitions into bounded political and regional consequences.
 * Effects are emitted only on a stage transition, never on every subsequent day in the same stage.
 */
internal object CrisisConsequenceManager {
    private val neighbors = mapOf(
        0 to setOf(1, 2, 3),
        1 to setOf(0),
        2 to setOf(0),
        3 to setOf(0)
    )

    fun resolve(
        names: List<String>,
        previousDays: List<Int>,
        currentDays: List<Int>
    ): List<CrisisTransition> {
        val count = minOf(names.size, previousDays.size, currentDays.size)
        return (0 until count).mapNotNull { index ->
            val from = stage(previousDays[index])
            val to = stage(currentDays[index])
            if (to.ordinal <= from.ordinal) return@mapNotNull null
            val name = names[index]
            val isPort = name.contains("порт", ignoreCase = true)
            val crown = when (to) {
                ProvinceCrisisStage.UNREST -> -1
                ProvinceCrisisStage.ESCALATING -> -2
                ProvinceCrisisStage.REVOLT -> -4
                ProvinceCrisisStage.STABLE -> 0
            }
            val people = when (to) {
                ProvinceCrisisStage.UNREST -> 0
                ProvinceCrisisStage.ESCALATING -> -1
                ProvinceCrisisStage.REVOLT -> 2
                ProvinceCrisisStage.STABLE -> 0
            }
            val merchant = if (isPort && to == ProvinceCrisisStage.REVOLT) -2 else 0
            val rose = if (to == ProvinceCrisisStage.REVOLT) 1 else 0
            val regionalLoss = when (to) {
                ProvinceCrisisStage.ESCALATING -> 1
                ProvinceCrisisStage.REVOLT -> 2
                else -> 0
            }
            val transitionText = when (to) {
                ProvinceCrisisStage.UNREST -> "В «$name» начались волнения. Доверие к короне пошатнулось."
                ProvinceCrisisStage.ESCALATING -> "Кризис в «$name» перерос в открытое неповиновение. Соседние земли ощущают напряжение."
                ProvinceCrisisStage.REVOLT -> "В «$name» вспыхнуло восстание. Весть разошлась по королевству и ослабила соседние общины."
                ProvinceCrisisStage.STABLE -> return@mapNotNull null
            }
            CrisisTransition(index, name, from, to, crown, merchant, people, rose, regionalLoss, transitionText)
        }
    }

    fun affectedNeighbors(provinceIndex: Int, provinceCount: Int): List<Int> =
        neighbors[provinceIndex].orEmpty().filter { it in 0 until provinceCount }.sorted()

    private fun stage(days: Int): ProvinceCrisisStage = when (days.coerceIn(0, 3)) {
        0 -> ProvinceCrisisStage.STABLE
        1 -> ProvinceCrisisStage.UNREST
        2 -> ProvinceCrisisStage.ESCALATING
        else -> ProvinceCrisisStage.REVOLT
    }
}
