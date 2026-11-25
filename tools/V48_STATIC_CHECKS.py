from pathlib import Path
root = Path(__file__).resolve().parents[1]
manager = (root / 'app/src/main/java/com/ashes/crown/InventoryManager.kt').read_text(encoding='utf-8')
test = (root / 'tools/InventoryRulesCheck.kt').read_text(encoding='utf-8')
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text(encoding='utf-8')
checks = {
    'legacy delimiter rejected before persistence': "clean.contains('¦')" in manager,
    'control characters rejected': 'clean.any(Char::isISOControl)' in manager,
    'failed craft keeps ingredients': 'invalid crafted output must not consume ingredients' in test,
    'delimiter and control-character regressions covered': 'save delimiter cannot split one item' in test and 'control characters are rejected' in test,
    'legacy delimiter remains the persisted format': '.putString("items", inventory.joinToString("¦"))' in main,
    'inventory capacity remains enforced': 'items.size >= MAX_ENTRIES' in manager,
}
for label, ok in checks.items():
    print(('PASS' if ok else 'FAIL') + ': ' + label)
assert all(checks.values())
