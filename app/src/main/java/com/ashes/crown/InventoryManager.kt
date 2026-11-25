package com.ashes.crown

/** Stable item groups used by inventory, equipment and crafting rules. */
enum class InventoryCategory {
    WEAPON, ARMOR, CONSUMABLE, MATERIAL, QUEST, DOCUMENT, TREASURE, MISC
}

data class InventoryChange(
    val items: List<String>,
    val changed: Boolean,
    val message: String = ""
)

/**
 * Pure inventory rules. The persisted representation remains the legacy delimited
 * String list so existing Alpha 27 saves remain readable without a schema migration.
 */
internal object InventoryManager {
    const val MAX_ENTRIES = 200

    private val weapons = setOf("Охотничий клинок", "Меч стражника", "Тяжёлый палаш")
    private val armor = setOf("Кольчуга стражника", "Усиленная кольчуга")
    private val consumables = setOf("Лечебный бальзам", "Лечебная трава", "Дорожные припасы", "Свеча погребального обряда")
    private val materials = setOf("Железный обломок", "Обломок старой печати", "Кость с руной", "Затупленный наконечник")
    private val documents = setOf(
        "Пустой блокнот", "Записка старого ветерана", "Половина шифрованного письма",
        "Список ночных грузов", "Список пропавших жителей", "Копия портовой накладной",
        "Записка с гербом дома", "Обрывок неизвестного шифра", "План старого архива",
        "Реестр тайных погребений", "Письмо истинного наследника", "Морская карта Веймара",
        "Слух о корабле без печати"
    )
    private val stackable = setOf("Лечебный бальзам", "Лечебная трава", "Дорожные припасы", "Железный обломок")

    fun category(item: String): InventoryCategory = when (item) {
        in weapons -> InventoryCategory.WEAPON
        in armor -> InventoryCategory.ARMOR
        in consumables -> InventoryCategory.CONSUMABLE
        in materials -> InventoryCategory.MATERIAL
        in documents -> InventoryCategory.DOCUMENT
        "Архивный ключ", "Медный ключ без короны", "Ржавый ключ подземелья", "Печать старой часовни", "Знак Совета семи домов", "Королевский медальон", "Перстень смотрителя склепа" -> InventoryCategory.QUEST
        "Серебряная закладка", "Медная торговая метка", "Знак клана горцев" -> InventoryCategory.TREASURE
        else -> InventoryCategory.MISC
    }

    fun normalize(items: List<String>): List<String> = items.asSequence()
        .map(String::trim)
        .filter(String::isNotEmpty)
        .toList()

    fun add(items: List<String>, item: String): InventoryChange {
        val clean = item.trim()
        if (clean.isEmpty()) return InventoryChange(items, false, "Пустой предмет нельзя добавить.")
        // The legacy save format joins names with this delimiter. Accepting it inside
        // an item silently turns one item into several entries after the next reload.
        if (clean.contains('¦') || clean.any(Char::isISOControl)) {
            return InventoryChange(items, false, "Название предмета содержит недопустимые символы.")
        }
        if (items.size >= MAX_ENTRIES) return InventoryChange(items, false, "Сумка заполнена: достигнут предел в $MAX_ENTRIES предметов.")
        if (clean !in stackable && clean in items) return InventoryChange(items, false, "Этот уникальный предмет уже есть в сумке.")
        return InventoryChange(items + clean, true)
    }

    fun removeOne(items: List<String>, item: String): InventoryChange {
        val index = items.indexOf(item)
        if (index < 0) return InventoryChange(items, false, "Предмет не найден.")
        return InventoryChange(items.toMutableList().also { it.removeAt(index) }, true)
    }

    fun removeAll(items: List<String>, item: String): InventoryChange {
        val next = items.filterNot { it == item }
        return InventoryChange(next, next.size != items.size)
    }

    /** Applies quest item consumption and rewards atomically: a full bag cannot half-complete a quest. */
    fun applyQuestDelta(items: List<String>, removeItems: List<String>, addItems: List<String>): InventoryChange {
        // Removal entries are required quest evidence/consumables. Do not complete a
        // transition when a required item is absent or requested more times than owned.
        val requiredCounts = removeItems.groupingBy { it }.eachCount()
        val missing = requiredCounts.entries.firstOrNull { (item, amount) -> count(items, item) < amount }
        if (missing != null) {
            return InventoryChange(items, false, "Не найден обязательный предмет: ${missing.key}.")
        }
        var candidate = items
        removeItems.forEach { item -> candidate = removeOne(candidate, item).items }
        addItems.forEach { item ->
            val clean = item.trim()
            if (clean.isNotEmpty() && clean !in stackable && clean in candidate) return@forEach
            val added = add(candidate, item)
            if (!added.changed) return InventoryChange(items, false, added.message)
            candidate = added.items
        }
        return InventoryChange(candidate, candidate != items)
    }

    fun count(items: List<String>, item: String): Int = items.count { it == item }
    fun contains(items: List<String>, item: String): Boolean = item in items

    /** All requirements are checked before any ingredient is consumed. */
    fun craft(items: List<String>, requirements: Map<String, Int>, output: String): InventoryChange {
        if (requirements.isEmpty() || requirements.any { (item, amount) -> item.isBlank() || amount <= 0 || count(items, item) < amount }) {
            return InventoryChange(items, false, "Недостаточно материалов для изготовления.")
        }
        var next = items
        requirements.forEach { (item, amount) -> repeat(amount) { next = removeOne(next, item).items } }
        val added = add(next, output)
        if (!added.changed) return InventoryChange(items, false, added.message)
        return InventoryChange(added.items, true, "Предмет изготовлен.")
    }
}
