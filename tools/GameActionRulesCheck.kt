package com.ashes.crown

/** Regression checks for deterministic story-choice resolution. */
object GameActionRulesCheck {
    @JvmStatic fun main(args: Array<String>) {
        val initial = GameChoiceState(0, 20, 0, 18, 100, 0, 0, 0, 0, 0)
        val paidChoice = GameChoice(
            title = "Заплатить торговцу", detail = "Проверка стоимости", outcome = "Сделка заключена",
            gold = -5, reputation = 2, guard = -30, seleste = 150, health = -120,
            influence = 3, crown = 150, merchants = -150, commoners = 4, rose = -4,
            nextScene = 1
        )
        val accepted = GameActionRules.resolveChoice(initial, paidChoice)
        check(accepted.accepted)
        check(accepted.state.gold == 13)
        check(accepted.state.reputation == 2)
        check(accepted.state.guard == 0)
        check(accepted.state.seleste == 100)
        check(accepted.state.health == 0)
        check(accepted.state.influence == 3)
        check(accepted.state.crownRep == 100)
        check(accepted.state.merchantRep == -100)
        check(accepted.state.peopleRep == 4)
        check(accepted.state.roseRep == -4)

        val tooExpensive = GameChoice(
            title = "Невозможная покупка", detail = "Недостаточно средств", outcome = "Не должно выполниться",
            gold = -19, nextScene = 2
        )
        val rejected = GameActionRules.resolveChoice(initial, tooExpensive)
        check(!rejected.accepted)
        check(rejected.state == initial) { "Rejected choice must not mutate any campaign value" }
        check(rejected.rejectionMessage?.contains("требуется 19") == true)

        val freeChoice = GameChoice(
            title = "Помочь добровольно", detail = "Без стоимости", outcome = "Помощь оказана",
            gold = 0, health = 10, nextScene = 3
        )
        val freeResult = GameActionRules.resolveChoice(initial.copy(health = 95), freeChoice)
        check(freeResult.accepted && freeResult.state.health == 100)

        // V51: extreme deltas must saturate rather than wrap around.
        val overflowChoice = GameChoice(
            title = "Проверка границ", detail = "Экстремальные числа", outcome = "Проверка",
            gold = Int.MAX_VALUE, reputation = Int.MAX_VALUE, influence = Int.MAX_VALUE,
            guard = Int.MAX_VALUE, health = Int.MAX_VALUE, seleste = Int.MAX_VALUE,
            crown = Int.MAX_VALUE, merchants = Int.MIN_VALUE, commoners = Int.MAX_VALUE,
            rose = Int.MIN_VALUE, nextScene = 0
        )
        val overflowResult = GameActionRules.resolveChoice(
            initial.copy(reputation = Int.MAX_VALUE - 2, influence = Int.MAX_VALUE - 1, gold = 999_999),
            overflowChoice
        )
        check(overflowResult.accepted)
        check(overflowResult.state.reputation == 100)
        check(overflowResult.state.influence == 100)
        check(overflowResult.state.gold == 1_000_000)
        check(overflowResult.state.guard == 100 && overflowResult.state.health == 100)
        check(overflowResult.state.seleste == 100 && overflowResult.state.merchantRep == -100)
        check(overflowResult.state.roseRep == -100)

        val impossibleMinCost = GameActionRules.resolveChoice(
            initial.copy(gold = Int.MAX_VALUE),
            GameChoice("Недостижимая цена", "Проверка Int.MIN_VALUE", "Не должно пройти", gold = Int.MIN_VALUE, nextScene = 0)
        )
        check(!impossibleMinCost.accepted)
        check(impossibleMinCost.state == initial.copy(gold = Int.MAX_VALUE))

        val fullInventory = List(InventoryManager.MAX_ENTRIES) { "Старый предмет $it" }
        val itemChoice = GameChoice("Взять улику", "Нужен свободный слот", "Улика получена", gold = 3,
            itemReward = "Медный ключ без короны", nextScene = 9)
        val rejectedForSpace = GameActionRules.resolveChoiceWithInventory(initial, itemChoice, fullInventory)
        check(!rejectedForSpace.accepted)
        check(rejectedForSpace.state == initial)
        check(rejectedForSpace.inventory == fullInventory)
        val itemAccepted = GameActionRules.resolveChoiceWithInventory(initial, itemChoice, emptyList())
        check(itemAccepted.accepted && itemAccepted.inventory == listOf("Медный ключ без короны"))
        val alreadyOwned = GameActionRules.resolveChoiceWithInventory(initial, itemChoice, listOf("Медный ключ без короны"))
        check(alreadyOwned.accepted && alreadyOwned.inventory.size == 1)
        println("GameActionRules: costs, atomic item rewards, immutable rejection, bounded resources and integer-overflow boundaries passed")
    }
}
