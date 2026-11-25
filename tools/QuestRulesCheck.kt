package com.ashes.crown

private fun check(condition: Boolean, message: String) {
    if (!condition) error(message)
}

fun main() {
    val fresh = MedicineQuestState(stage = 0, crateFound = false)
    val accepted = QuestManager.transitionMedicineQuest(fresh, MedicineQuestAction.ACCEPT)
    check(accepted.changed && accepted.state == MedicineQuestState(1, false), "accept quest")

    val prematureCompletion = QuestManager.transitionMedicineQuest(accepted.state, MedicineQuestAction.COMPLETE)
    check(!prematureCompletion.changed && prematureCompletion.goldReward == 0, "cannot complete without crate")

    val found = QuestManager.transitionMedicineQuest(accepted.state, MedicineQuestAction.FIND_CRATE)
    check(found.changed && found.state.crateFound, "find crate")
    check(found.addItems == listOf(QuestManager.MEDICINE_CRATE), "crate item reward")

    val duplicateFind = QuestManager.transitionMedicineQuest(found.state, MedicineQuestAction.FIND_CRATE)
    check(!duplicateFind.changed && duplicateFind.addItems.isEmpty(), "cannot duplicate crate")

    val completed = QuestManager.transitionMedicineQuest(found.state, MedicineQuestAction.COMPLETE)
    check(completed.changed && completed.state == MedicineQuestState(2, false), "complete quest")
    check(completed.goldReward == 8 && completed.commonerReputationReward == 4, "completion rewards")
    check(completed.removeItems == listOf(QuestManager.MEDICINE_CRATE), "consume crate")
    check(completed.addItems == listOf(QuestManager.EMPTY_CARGO_TAG), "completion evidence")

    val repeatedCompletion = QuestManager.transitionMedicineQuest(completed.state, MedicineQuestAction.COMPLETE)
    check(!repeatedCompletion.changed && repeatedCompletion.goldReward == 0, "no repeated completion reward")
    check(!QuestManager.transitionMedicineQuest(fresh, MedicineQuestAction.FIND_CRATE).changed, "cannot find before accepting")

    check(QuestManager.canAdvanceBlackRoseFromEvidence(0, true, false), "night cargo evidence opens quest")
    check(QuestManager.canAdvanceBlackRoseFromEvidence(0, false, true), "night ship rumor opens quest")
    check(!QuestManager.canAdvanceBlackRoseFromEvidence(0, false, false), "no evidence blocks quest")
    check(!QuestManager.canAdvanceBlackRoseFromEvidence(1, true, true), "quest cannot restart")
    check(QuestManager.canSearchOldChapel(1, "Старая часовня"), "chapel stage and location")
    check(!QuestManager.canSearchOldChapel(1, "Порт Веймар"), "wrong location blocks chapel")
    check(QuestManager.canEnterSevenHousesCouncil(5, true), "seal opens council")
    check(!QuestManager.canEnterSevenHousesCouncil(4, true), "unfinished rose quest blocks council")
    check(!QuestManager.canEnterSevenHousesCouncil(5, false), "missing seal blocks council")
    check(QuestManager.canChooseCouncilAlliance(1), "alliance selection stage")
    check(!QuestManager.canChooseCouncilAlliance(2), "alliance cannot be selected twice")
    check(QuestManager.canFinalizeCouncilAlliance(2, 1), "valid alliance finalization")
    check(!QuestManager.canFinalizeCouncilAlliance(2, 0), "no alliance cannot finalize")
    check(!QuestManager.canFinalizeCouncilAlliance(3, 1), "completed council cannot pay again")

    println("QuestRulesCheck: all assertions passed")
}
