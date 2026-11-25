package com.ashes.crown

/** Pure equipment lookup. Legacy inventory strings remain the persisted source of truth. */
internal object EquipmentManager {
    data class EquipmentProfile(
        val weaponName: String,
        val attackBonus: Int,
        val armorName: String,
        val armorBonus: Int
    )

    fun profile(items: List<String>): EquipmentProfile {
        val weapon = when {
            InventoryManager.contains(items, "Тяжёлый палаш") -> "Тяжёлый палаш" to 7
            InventoryManager.contains(items, "Меч стражника") -> "Меч стражника" to 4
            InventoryManager.contains(items, "Охотничий клинок") -> "Охотничий клинок" to 3
            else -> "Архивный кинжал" to 1
        }
        val armor = when {
            InventoryManager.contains(items, "Усиленная кольчуга") -> "Усиленная кольчуга" to 5
            InventoryManager.contains(items, "Кольчуга стражника") -> "Кольчуга стражника" to 3
            else -> "Дорожный плащ" to 0
        }
        return EquipmentProfile(weapon.first, weapon.second, armor.first, armor.second)
    }

    fun attackBonus(items: List<String>): Int = profile(items).attackBonus
    fun armorBonus(items: List<String>): Int = profile(items).armorBonus
}
