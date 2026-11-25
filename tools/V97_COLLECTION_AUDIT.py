from pathlib import Path
root = Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text()
layout = (root / 'app/src/main/java/com/ashes/crown/CollectionLayoutRules.kt').read_text()
checks = {
    'inventory uses shared responsive column rule': 'val columns = CollectionLayoutRules.columns(maxWidth.value.toInt())' in main[main.index('private fun InventoryPageContent'):main.index('private fun JournalPageContent')],
    'relationships use shared responsive column rule': 'val columns = CollectionLayoutRules.columns(maxWidth.value.toInt())' in main[main.index('private fun PeoplePageContent'):main.index('private fun InventoryPageContent')],
    'shared breakpoint remains 560dp': 'availableWidthDp >= 560' in layout,
    'inventory is grouped by semantic category': 'inventory.groupingBy { it }.eachCount().entries.groupBy { InventoryManager.category(it.key) }' in main,
    'stack counts are visibly represented': 'Text("×${entry.value}"' in main,
    'empty inventory has a user-facing state': 'Сумка пуста.' in main,
}
for name, ok in checks.items(): print(('PASS' if ok else 'FAIL') + ': ' + name)
assert all(checks.values())
