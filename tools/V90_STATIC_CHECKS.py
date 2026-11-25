from pathlib import Path
import re

root = Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text(encoding='utf-8')
checks = []
def require(condition, message):
    if not condition:
        raise SystemExit('FAIL: ' + message)
    checks.append(message)

require('CollectionLayoutRules.columns(maxWidth.value.toInt())' in main, 'Compose uses the shared responsive width rule (dp)')
require('val columns = CollectionLayoutRules.columns(maxWidth.value.toInt())' in main[main.index('private fun InventoryPageContent'):main.index('private fun JournalPageContent')], 'inventory adapts to one/two columns')
require('entries.chunked(columns)' in main, 'inventory items are laid out in rows')
require('relationCards.chunked(columns)' in main, 'relationship cards are laid out in rows')
require('RelationPortraitData' in main, 'relationship card data is separated from rendering')
require('modifier.widthIn(min = 0.dp)' in main, 'map nodes no longer have a restrictive fixed maximum width')
require('modifier = Modifier.weight(1f)' in main, 'paired map nodes share available row width')
require('"world", "realm_map", "items", "people", "companions", "dialogues"' in main, 'map, inventory and relationship sections remain full-width pages')
require('ContentScale.Crop' in main, 'existing artwork crop behavior remains explicit')

rules = (root / 'app/src/main/java/com/ashes/crown/CollectionLayoutRules.kt').read_text(encoding='utf-8')
require('availableWidthDp >= 560' in rules, 'pure responsive breakpoint is defined')
test = (root / 'tools/CollectionLayoutRulesCheck.kt').read_text(encoding='utf-8')
require('for (items in 0..100)' in test, 'collection layout tests cover 0..100 items')

# Validate every drawable reference in MainActivity against packaged Android resources.
refs = set(re.findall(r'R\.drawable\.([A-Za-z0-9_]+)', main))
res = root / 'app/src/main/res'
available = {p.stem for folder in res.iterdir() if folder.is_dir() and folder.name.startswith('drawable') for p in folder.iterdir() if p.is_file()}
missing = sorted(refs - available)
require(not missing, 'all drawable references resolve (' + str(len(refs)) + ')')

print(f'V90_STATIC_CHECKS: PASS ({len(checks)} checks)')
for item in checks:
    print(' - ' + item)
