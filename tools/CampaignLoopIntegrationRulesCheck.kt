package com.ashes.crown

/**
 * Cross-system smoke scenario: story choice -> quest/inventory -> relationship -> travel
 * -> battle/reward -> complete current-schema snapshot validation.
 * This exercises domain rules together; it does not simulate Android UI or disk I/O.
 */
object CampaignLoopIntegrationRulesCheck {
    @JvmStatic fun main(args: Array<String>) {
        // Start a small campaign with a narrative decision and an item reward.
        var campaign = GameChoiceState(
            reputation = 0, guard = 20, seleste = 0, gold = 20, health = 100,
            influence = 0, crownRep = 0, merchantRep = 0, peopleRep = 0, roseRep = 0
        )
        var inventory = listOf("Лечебная трава", "Лечебная трава")
        val choice = GameChoice(
            title = "Поддержать портовых жителей", detail = "Проверка связанного цикла",
            outcome = "Ты помог жителям порта", gold = -3, commoners = 2,
            itemReward = "Пустой блокнот", nextScene = 1
        )
        val choiceResult = GameActionRules.resolveChoiceWithInventory(campaign, choice, inventory)
        check(choiceResult.accepted)
        campaign = choiceResult.state
        inventory = choiceResult.inventory
        check(campaign.gold == 17 && campaign.peopleRep == 2)
        check("Пустой блокнот" in inventory)

        // Quest lifecycle must add the evidence, then consume exactly that evidence on completion.
        var quest = MedicineQuestState(stage = 0, crateFound = false)
        val accepted = QuestManager.transitionMedicineQuest(quest, MedicineQuestAction.ACCEPT)
        check(accepted.changed)
        quest = accepted.state
        val found = QuestManager.transitionMedicineQuest(quest, MedicineQuestAction.FIND_CRATE)
        check(found.changed && found.addItems == listOf(QuestManager.MEDICINE_CRATE))
        inventory = InventoryManager.applyQuestDelta(inventory, emptyList(), found.addItems).also {
            check(it.changed)
        }.items
        quest = found.state
        val completed = QuestManager.transitionMedicineQuest(quest, MedicineQuestAction.COMPLETE)
        check(completed.changed && completed.removeItems == listOf(QuestManager.MEDICINE_CRATE))
        val questInventory = InventoryManager.applyQuestDelta(inventory, completed.removeItems, completed.addItems)
        check(questInventory.changed)
        inventory = questInventory.items
        campaign = campaign.copy(
            gold = campaign.gold + completed.goldReward,
            peopleRep = campaign.peopleRep + completed.commonerReputationReward
        )
        check(QuestManager.transitionMedicineQuest(completed.state, MedicineQuestAction.COMPLETE).changed.not())
        check(QuestManager.MEDICINE_CRATE !in inventory && QuestManager.EMPTY_CARGO_TAG in inventory)
        check(campaign.gold == 25 && campaign.peopleRep == 6)

        // Resolve a companion-to-companion scene and retain its two-sided affinity result.
        val relation = CompanionRelationshipManager.resolve(
            CompanionPair.GARRETT_CELESTE, 10, 12, CompanionRelationshipChoice.MEDIATE
        )
        check(relation.firstScore == 14 && relation.secondScore == 16)

        // Travel route and combat are resolved deterministically before progress is persisted.
        val route = TravelManager.findRoute("Королевский замок", "Порт Веймар")
            ?: error("Expected reachable port route")
        check(route.totalHours == 5 && route.isDangerous)
        val attack = BattleManager.playerAttack(
            baseRoll = 12, weaponBonus = 3, powerSkill = 2, woundPenalty = 0, enemyHealth = 19
        )
        check(attack.defeated && attack.enemyHealthAfter == 0)
        check(BattleManager.canResolveVictory("Наёмник Чёрной розы", attack.enemyHealthAfter))
        val reward = BattleManager.rewards("Наёмник Чёрной розы", false, 0)
        check(reward.eligible && reward.experience == 35 && reward.gold == 5)
        campaign = campaign.copy(gold = campaign.gold + reward.gold)

        // Build a full schema-11 primitive snapshot using the same required-key registry as saves.
        val stringFields = setOf(
            "a11_report", "a13_loyalty", "a13_prosperity", "a13_report", "a13_stewards",
            "a13_tax_days", "a15_crisis_days", "battle_enemy", "battle_log", "companion",
            "companion_moment_report", "companion_relation_report", "companion_story_report",
            "companion_team_event_report", "council_report", "dialog_npc", "expedition_note",
            "expedition_report", "frontier_report", "items", "journal", "location", "outcome",
            "social_event", "world_event"
        )
        val booleanFields = setOf("boss_defeated", "chosen", "quest_medicine_crate_found")
        val snapshot = SaveManager.currentSchemaRequiredKeys.associateWith { key ->
            when {
                key in stringFields -> ""
                key in booleanFields -> false
                else -> 0
            }
        }.toMutableMap()
        snapshot["save_schema_version"] = SaveManager.CURRENT_SCHEMA
        snapshot["scene"] = 1
        snapshot["gold"] = campaign.gold
        snapshot["hp"] = campaign.health
        snapshot["rep"] = campaign.reputation
        snapshot["f_people"] = campaign.peopleRep
        snapshot["world_day"] = 1
        snapshot["a14_upkeep_day"] = 1
        snapshot["location"] = "Порт Веймар"
        snapshot["items"] = inventory.joinToString("¦")
        snapshot["journal"] = listOf(accepted.journalEntry, found.journalEntry, completed.journalEntry)
            .filterNotNull().joinToString("¦")
        snapshot["quest_market"] = completed.state.stage
        snapshot["relation_garrett_celeste"] = relation.firstScore
        snapshot["experience"] = reward.experience
        snapshot["battle_wins"] = 1
        snapshot["battle_enemy"] = ""
        snapshot["battle_enemy_hp"] = 0

        val validated = SaveManager.validateSnapshot(snapshot, 18)
        check(validated is SaveValidationResult.Accepted) {
            "Integrated campaign snapshot should validate: $validated"
        }
        val restored = (validated as SaveValidationResult.Accepted).values
        check(restored["gold"] == campaign.gold)
        check(restored["location"] == "Порт Веймар")
        check(restored["items"] == inventory.joinToString("¦"))
        check(restored["relation_garrett_celeste"] == 14)
        check(restored["experience"] == 35)

        // A corrupted day marker must fail closed rather than silently loading a mixed-day campaign.
        val corrupted = snapshot.toMutableMap().apply { put("a14_upkeep_day", 99) }
        check(SaveManager.validateSnapshot(corrupted, 18) is SaveValidationResult.Rejected)

        println("CampaignLoopIntegrationRulesCheck: narrative, quest, inventory, companion affinity, travel, combat rewards and schema-11 save round-trip passed")
    }
}
