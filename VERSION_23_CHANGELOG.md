# Ashes of the Crown — Working Version 23

## Inventory domain extraction and categorized inventory

- Added pure Kotlin `InventoryManager.kt` with stable item categories: weapons, armor, consumables, materials, quest items, documents/evidence, valuables and miscellaneous items.
- Centralized inventory normalization, unique-item protection, stackable material/consumable handling, one-item removal, remove-all, count/contains queries and atomic crafting.
- Routed the existing `MainActivity.kt` inventory additions/removals and item queries through the manager. The legacy string-list save representation is retained; no save keys or schema version were changed.
- Updated the inventory page to group the player's current items by category and show stack quantities.
- Converted both existing crafting actions to all-or-nothing recipes: ingredients are checked first and are not consumed if requirements or output insertion fail.
- Added `InventoryRulesCheck.kt` covering legacy normalization, duplicate unique items, stackable items, category mapping, recipe success/failure, ingredient preservation, item removal and the new-item entry ceiling.

## Verification

- `RulesCheck.kt`: passed, including 2,000 deterministic realm-day differential cases.
- `QuestRulesCheck.kt`: passed.
- `InventoryRulesCheck.kt`: passed.
- Android/Compose build, APK assembly, emulator and device tests were not run. The environment still lacks the Android SDK/Gradle wrapper required for a real application build.

## Compatibility and boundaries

- Save schema remains version 7; existing inventory values remain in the same `items` preference as a `¦`-delimited list.
- Legacy inventory entries are not truncated during normalization. New additions are refused once the inventory reaches 200 entries.
- This update introduces categories and reliable inventory operations, not yet a full equipment screen, drag-and-drop, item tooltips, sale prices or item-use UI.
