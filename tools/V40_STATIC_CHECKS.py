from pathlib import Path
import xml.etree.ElementTree as ET
root = Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text(encoding='utf-8')
manager = (root / 'app/src/main/java/com/ashes/crown/WorldCrisisManager.kt').read_text(encoding='utf-8')
save = (root / 'app/src/main/java/com/ashes/crown/SaveManager.kt').read_text(encoding='utf-8')
checks = {
    'four crisis stages are modeled': all(x in manager for x in ('STABLE', 'UNREST', 'ESCALATING', 'REVOLT')),
    'crisis severity is derived from existing day counters': 'crisisDays[index].coerceIn(0, 3)' in manager,
    'crisis reports rank urgent provinces': 'mostUrgent' in manager and 'maxWithOrNull' in manager,
    'each crisis stage provides recovery guidance': 'recommendedAction' in manager and all(x in manager for x in ('Поддерживай процветание', 'переговоры', 'доставь помощь')),
    'daily realm processing integrates crisis summary': 'WorldCrisisManager.evaluate(' in main and 'crisisSummary = crisisReport.summary' in main,
    'province screen displays stage-specific guidance': 'crisisStatus.recommendedAction' in main and 'ProvinceCrisisStage.ESCALATING' in main,
    'no additional save schema migration introduced': 'CURRENT_SCHEMA = 11' in save,
}
for label, ok in checks.items(): print(('PASS' if ok else 'FAIL') + ': ' + label)
assert all(checks.values())
ET.parse(root / 'app/src/main/AndroidManifest.xml')
print('PASS: AndroidManifest.xml parses')
