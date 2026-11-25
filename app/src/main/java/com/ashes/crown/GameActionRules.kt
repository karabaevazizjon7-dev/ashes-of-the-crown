package com.ashes.crown

internal data class GameChoice(
    val title: String, val detail: String, val outcome: String,
    val reputation: Int = 0, val guard: Int = 0, val seleste: Int = 0,
    val gold: Int = 0, val health: Int = 0, val influence: Int = 0,
    val crown: Int = 0, val merchants: Int = 0, val commoners: Int = 0, val rose: Int = 0,
    val itemReward: String? = null, val nextScene: Int
)

/** Immutable campaign values consumed by a narrative choice. */
internal data class GameChoiceState(
    val reputation: Int,
    val guard: Int,
    val seleste: Int,
    val gold: Int,
    val health: Int,
    val influence: Int,
    val crownRep: Int,
    val merchantRep: Int,
    val peopleRep: Int,
    val roseRep: Int
)

/** Result of resolving a story choice without touching UI or persistence. */
internal data class GameChoiceResolution(
    val accepted: Boolean,
    val state: GameChoiceState,
    val rejectionMessage: String? = null
)

internal data class GameChoiceTransaction(
    val accepted: Boolean,
    val state: GameChoiceState,
    val inventory: List<String>,
    val rejectionMessage: String? = null
)

/**
 * Owns the deterministic rules for narrative choices. Compose is responsible only
 * for applying the returned state, displaying messages, updating inventory and saving.
 */
internal object GameActionRules {
    private const val MAX_GOLD = ResourceRules.MAX_GOLD

    /** Adds using Long so hostile/extreme values cannot wrap around before clamping. */
    private fun addBounded(value: Int, delta: Int, min: Int, max: Int): Int =
        (value.toLong() + delta.toLong()).coerceIn(min.toLong(), max.toLong()).toInt()

    fun resolveChoice(state: GameChoiceState, choice: GameChoice): GameChoiceResolution {
        // Negating Int.MIN_VALUE in Int arithmetic wraps to itself and used to turn
        // an impossible price into a free choice. Keep the price in Long instead.
        val requiredGold = -choice.gold.toLong()
        if (requiredGold > 0L && state.gold.toLong() < requiredGold) {
            return GameChoiceResolution(
                accepted = false,
                state = state,
                rejectionMessage = "Недостаточно золота: требуется $requiredGold, в казне ${state.gold}. Выбор не выполнен."
            )
        }
        return GameChoiceResolution(
            accepted = true,
            state = GameChoiceState(
                reputation = addBounded(state.reputation, choice.reputation, -100, 100),
                guard = addBounded(state.guard, choice.guard, 0, 100),
                seleste = addBounded(state.seleste, choice.seleste, -100, 100),
                gold = addBounded(state.gold, choice.gold, 0, MAX_GOLD),
                health = addBounded(state.health, choice.health, 0, 100),
                influence = addBounded(state.influence, choice.influence, 0, ResourceRules.MAX_INFLUENCE),
                crownRep = addBounded(state.crownRep, choice.crown, -100, 100),
                merchantRep = addBounded(state.merchantRep, choice.merchants, -100, 100),
                peopleRep = addBounded(state.peopleRep, choice.commoners, -100, 100),
                roseRep = addBounded(state.roseRep, choice.rose, -100, 100)
            )
        )
    }
    /** Resolves a narrative choice and its item reward as one all-or-nothing operation. */
    fun resolveChoiceWithInventory(
        state: GameChoiceState,
        choice: GameChoice,
        inventory: List<String>
    ): GameChoiceTransaction {
        val resolution = resolveChoice(state, choice)
        if (!resolution.accepted) {
            return GameChoiceTransaction(false, state, inventory, resolution.rejectionMessage)
        }
        val reward = choice.itemReward
        if (reward.isNullOrBlank() || reward in inventory) {
            return GameChoiceTransaction(true, resolution.state, inventory.toList())
        }
        val inventoryChange = InventoryManager.add(inventory, reward)
        if (!inventoryChange.changed) {
            return GameChoiceTransaction(false, state, inventory, inventoryChange.message)
        }
        return GameChoiceTransaction(true, resolution.state, inventoryChange.items)
    }

}
