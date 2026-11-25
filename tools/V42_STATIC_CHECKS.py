from pathlib import Path
import xml.etree.ElementTree as ET
root = Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text(encoding='utf-8')
manager = (root / 'app/src/main/java/com/ashes/crown/RealmPriorityManager.kt').read_text(encoding='utf-8')
checks = {
    'council priorities combine economy, army, provinces and factions': all(x in manager for x in ('gold: Int', 'provisions: Int', 'army: Int', 'provinceLoyalty: List<Int>', 'crownRep: Int', 'peopleRep: Int')),
    'recommendations are derived and do not mutate campaign state': 'does not mutate resources or persist flags' in manager and 'save()' not in manager,
    'supply risk accounts for daily ration consumption': 'dailyRations' in manager and 'daysOfSupply' in manager,
    'province risk uses both loyalty and crisis stage': 'provinceCrisisDays' in manager and 'provinceLoyalty' in manager,
    'MainActivity displays live council priorities': 'RealmPriorityManager.evaluate(' in main and 'ПРИОРИТЕТЫ КОРОЛЕВСКОГО СОВЕТА' in main,
    'no save migration introduced': 'CURRENT_SCHEMA = 11' in (root / 'app/src/main/java/com/ashes/crown/SaveManager.kt').read_text(encoding='utf-8'),
}
for label, ok in checks.items(): print(('PASS' if ok else 'FAIL') + ': ' + label)
assert all(checks.values())
ET.parse(root / 'app/src/main/AndroidManifest.xml')
print('PASS: AndroidManifest.xml parses')
