package com.ashes.crown

/** Persisted state for the medicine-crate side quest. Values map to the existing save keys. */
data class MedicineQuestState(
    val stage: Int,
    val crateFound: Boolean
)

enum class MedicineQuestAction { ACCEPT, FIND_CRATE, COMPLETE }

data class MedicineQuestTransition(
    val state: MedicineQuestState,
    val changed: Boolean,
    val outcome: String,
    val journalEntry: String? = null,
    val goldReward: Int = 0,
    val commonerReputationReward: Int = 0,
    val addItems: List<String> = emptyList(),
    val removeItems: List<String> = emptyList()
)

/**
 * Deterministic quest rules extracted from the Compose screen.
 * This manager has no Android/Compose dependencies and does not mutate game state.
 */
internal object QuestManager {
    const val MEDICINE_CRATE = "Ящик с лекарствами"
    const val EMPTY_CARGO_TAG = "Пустая грузовая бирка"
    const val MEDICINE_REWARD_GOLD = 8
    const val MEDICINE_REWARD_COMMONER_REPUTATION = 4

    fun transitionMedicineQuest(
        current: MedicineQuestState,
        action: MedicineQuestAction
    ): MedicineQuestTransition {
        val boundedStage = current.stage.coerceIn(0, 2)
        val safe = MedicineQuestState(
            stage = boundedStage,
            crateFound = boundedStage == 1 && current.crateFound
        )
        return when (action) {
            MedicineQuestAction.ACCEPT -> when (safe.stage) {
                0 -> MedicineQuestTransition(
                    state = MedicineQuestState(stage = 1, crateFound = false),
                    changed = true,
                    outcome = "Мира просит найти ящик с лекарствами, исчезнувший по дороге к порту. Она обещает заплатить 8 золотых.",
                    journalEntry = "Задание принято: найти пропавший ящик с лекарствами."
                )
                else -> blocked(safe, "Это задание уже принято или завершено.")
            }
            MedicineQuestAction.FIND_CRATE -> when {
                safe.stage != 1 -> blocked(safe, "Сначала возьми у Миры задание о пропавших лекарствах.")
                safe.crateFound -> blocked(safe, "Ящик уже найден. Вернись к Мире, чтобы передать его.")
                else -> MedicineQuestTransition(
                    state = safe.copy(crateFound = true),
                    changed = true,
                    outcome = "Ты нашёл пропавший ящик с лекарствами на дальнем складе. Теперь можно вернуться к Мире и передать его.",
                    journalEntry = "В порту найден пропавший ящик с лекарствами.",
                    addItems = listOf(MEDICINE_CRATE)
                )
            }
            MedicineQuestAction.COMPLETE -> when {
                safe.stage != 1 -> blocked(safe, "Нет активного задания, которое можно завершить.")
                !safe.crateFound -> blocked(safe, "Сначала найди ящик с лекарствами в порту и осмотри его.")
                else -> MedicineQuestTransition(
                    state = MedicineQuestState(stage = 2, crateFound = false),
                    changed = true,
                    outcome = "Ты нашёл ящик и вернул лекарства. Мира заплатила 8 золотых; народная поддержка выросла.",
                    journalEntry = "Задание выполнено: ящик с лекарствами возвращён.",
                    goldReward = MEDICINE_REWARD_GOLD,
                    commonerReputationReward = MEDICINE_REWARD_COMMONER_REPUTATION,
                    addItems = listOf(EMPTY_CARGO_TAG),
                    removeItems = listOf(MEDICINE_CRATE)
                )
            }
        }
    }

    fun canAdvanceBlackRoseFromEvidence(stage: Int, hasNightCargoList: Boolean, hasNightShipRumor: Boolean): Boolean =
        stage == 0 && (hasNightCargoList || hasNightShipRumor)

    fun canSearchOldChapel(stage: Int, currentLocation: String): Boolean =
        stage == 1 && currentLocation == "Старая часовня"

    fun canEnterSevenHousesCouncil(roseQuestStage: Int, hasChapelSeal: Boolean): Boolean =
        roseQuestStage >= 5 && hasChapelSeal

    fun canChooseCouncilAlliance(stage: Int): Boolean = stage == 1
    fun canFinalizeCouncilAlliance(stage: Int, path: Int): Boolean = stage == 2 && path in 1..3

    private fun blocked(state: MedicineQuestState, message: String) =
        MedicineQuestTransition(state = state, changed = false, outcome = message)
}
