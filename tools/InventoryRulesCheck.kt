package com.ashes.crown

private fun expect(condition: Boolean, message: String) {
    if (!condition) error(message)
}

fun main() {
    val clean = InventoryManager.normalize(listOf(" Архивный ключ ", "", "Дорожный плащ", "Архивный ключ"))
    expect(clean == listOf("Архивный ключ", "Дорожный плащ", "Архивный ключ"), "legacy normalization preserves entries and order")

    val unique = InventoryManager.add(clean, "Архивный ключ")
    expect(!unique.changed && unique.items == clean, "unique item cannot be duplicated")
    val delimiterInjection = InventoryManager.add(clean, "Поддельный¦предмет")
    expect(!delimiterInjection.changed && delimiterInjection.items == clean, "save delimiter cannot split one item into multiple entries")
    val controlCharacter = InventoryManager.add(clean, "Предмет\nс переносом")
    expect(!controlCharacter.changed && controlCharacter.items == clean, "control characters are rejected")
    val stack1 = InventoryManager.add(clean, "Лечебная трава")
    val stack2 = InventoryManager.add(stack1.items, "Лечебная трава")
    expect(stack2.changed && InventoryManager.count(stack2.items, "Лечебная трава") == 2, "materials can stack")
    expect(InventoryManager.category("Меч стражника") == InventoryCategory.WEAPON, "weapon classification")
    expect(InventoryManager.category("Письмо истинного наследника") == InventoryCategory.DOCUMENT, "document classification")
    expect(InventoryManager.category("Лечебный бальзам") == InventoryCategory.CONSUMABLE, "consumable classification")

    val missing = InventoryManager.craft(clean, mapOf("Лечебная трава" to 2), "Лечебный бальзам")
    expect(!missing.changed && missing.items == clean, "failed craft must not consume ingredients")
    val ingredients = clean + listOf("Лечебная трава", "Лечебная трава")
    val crafted = InventoryManager.craft(ingredients, mapOf("Лечебная трава" to 2), "Лечебный бальзам")
    expect(crafted.changed, "valid craft succeeds")
    expect(InventoryManager.count(crafted.items, "Лечебная трава") == 0, "craft consumes exact ingredients")
    expect(InventoryManager.count(crafted.items, "Лечебный бальзам") == 1, "craft grants output")

    val alreadyEquipped = InventoryManager.craft(
        ingredients + "Усиленная кольчуга",
        mapOf("Лечебная трава" to 2),
        "Усиленная кольчуга"
    )
    expect(!alreadyEquipped.changed && alreadyEquipped.items == ingredients + "Усиленная кольчуга", "duplicate unique output preserves ingredients")
    val invalidOutput = InventoryManager.craft(ingredients, mapOf("Лечебная трава" to 2), "Подмена¦предмета")
    expect(!invalidOutput.changed && invalidOutput.items == ingredients, "invalid crafted output must not consume ingredients")

    val legacyLarge = List(InventoryManager.MAX_ENTRIES + 5) { "Старый предмет $it" }
    expect(InventoryManager.normalize(legacyLarge).size == legacyLarge.size, "legacy inventory is never truncated")
    val full = List(InventoryManager.MAX_ENTRIES) { "Предмет $it" }
    val overflow = InventoryManager.add(full, "Новый предмет")
    expect(!overflow.changed && overflow.items == full, "inventory entry limit")
    val blockedQuest = InventoryManager.applyQuestDelta(full, emptyList(), listOf("Печать старой часовни"))
    expect(!blockedQuest.changed && blockedQuest.items == full && blockedQuest.message.isNotBlank(), "quest reward cannot overflow a full inventory")
    val atomicQuest = InventoryManager.applyQuestDelta(full, listOf("Несуществующий предмет"), listOf("Знак Совета семи домов"))
    expect(!atomicQuest.changed && atomicQuest.items == full && atomicQuest.message.isNotBlank(), "failed quest reward leaves original inventory untouched")
    val replaceQuestItem = InventoryManager.applyQuestDelta(full, listOf(full.first()), listOf("Печать старой часовни"))
    expect(replaceQuestItem.changed && replaceQuestItem.items.size == full.size && "Печать старой часовни" in replaceQuestItem.items, "quest can atomically replace consumed evidence at capacity")
    val existingQuestItem = InventoryManager.applyQuestDelta(listOf("Печать старой часовни"), emptyList(), listOf("Печать старой часовни"))
    expect(existingQuestItem.message.isBlank() && existingQuestItem.items.size == 1, "existing unique quest evidence is treated as already delivered")
    val missingRequiredItem = InventoryManager.applyQuestDelta(
        listOf("Пустой блокнот"), listOf("Ящик с лекарствами"), listOf("Пустая грузовая бирка")
    )
    expect(!missingRequiredItem.changed && missingRequiredItem.items == listOf("Пустой блокнот"), "quest cannot complete when required evidence is missing")
    val duplicateRemoval = InventoryManager.applyQuestDelta(
        listOf("Ящик с лекарствами"), listOf("Ящик с лекарствами", "Ящик с лекарствами"), emptyList()
    )
    expect(!duplicateRemoval.changed && duplicateRemoval.items == listOf("Ящик с лекарствами"), "quest cannot consume the same required item more times than owned")
    val exactBatchRemoval = InventoryManager.applyQuestDelta(
        listOf("Лечебная трава", "Лечебная трава", "Лечебная трава"),
        listOf("Лечебная трава", "Лечебная трава"), emptyList()
    )
    expect(exactBatchRemoval.changed && exactBatchRemoval.items == listOf("Лечебная трава"), "quest consumes only the requested quantity from a stack")
    val partialBatch = InventoryManager.applyQuestDelta(full, listOf(full.first()), listOf("Печать старой часовни", "Второй новый уникальный знак"))
    expect(!partialBatch.changed && partialBatch.items == full && partialBatch.message.isNotBlank(), "multi-item quest transaction rolls back every partial change")
    expect(InventoryManager.removeOne(stack2.items, "Лечебная трава").items.count { it == "Лечебная трава" } == 1, "remove exactly one stack item")
    expect(InventoryManager.removeAll(stack2.items, "Лечебная трава").items.none { it == "Лечебная трава" }, "remove all matching items")

    println("InventoryRulesCheck: all assertions passed")
}
