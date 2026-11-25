from pathlib import Path
import re
root = Path(__file__).resolve().parents[1]
main = (root/'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text()
save = (root/'app/src/main/java/com/ashes/crown/SaveManager.kt').read_text()
checks = {
    'save schema remains unchanged at version 11': 'const val CURRENT_SCHEMA = 11' in save,
    'no schema 12 migration was introduced': 'const val CURRENT_SCHEMA = 12' not in save,
    'full-width map page remains in use': 'RenderPage_realm_map()' in main,
    'full-width inventory page remains in use': 'InventoryPageContent(gold = gold, inventory = inventory)' in main,
    'full-width relationships page remains in use': 'PeoplePageContent(' in main,
    'no APK has been added to the project tree': not any(root.rglob('*.apk')),
    'V96-V100 change and audit records exist': all((root/f'VERSION_{n}_{suffix}.md').exists() for n,suffix in [(96,'CHANGELOG'),(97,'CHANGELOG'),(98,'CHANGELOG'),(99,'CHANGELOG'),(100,'CHANGELOG'),(100,'AUDIT')]),
}
for name, ok in checks.items(): print(('PASS' if ok else 'FAIL') + ': ' + name)
assert all(checks.values())
