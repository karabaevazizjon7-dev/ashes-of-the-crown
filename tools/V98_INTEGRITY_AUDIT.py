from pathlib import Path
root = Path(__file__).resolve().parents[1]
manager = (root / 'app/src/main/java/com/ashes/crown/InventoryManager.kt').read_text()
test = (root / 'tools/InventoryRulesCheck.kt').read_text()
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text()
checks = {
    'quest evidence removal consumes one requested unit at a time': 'removeItems.forEach { item -> candidate = removeOne(candidate, item).items }' in manager,
    'quest transaction verifies required quantities before mutation': 'requiredCounts = removeItems.groupingBy { it }.eachCount()' in manager and 'count(items, item) < amount' in manager,
    'stacked quest consumption regression is tested': 'quest consumes only the requested quantity from a stack' in test,
    'failed transaction returns original inventory': 'return InventoryChange(items, false, added.message)' in manager,
    'story choice text is not truncated by maxLines': 'maxLines = 2' not in main[main.index('RenderPage_fallback'):main.index('PageArtworkPanel')],
}
for name, ok in checks.items(): print(('PASS' if ok else 'FAIL') + ': ' + name)
assert all(checks.values())
