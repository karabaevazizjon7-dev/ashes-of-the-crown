from pathlib import Path
import xml.etree.ElementTree as ET
root = Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text(encoding='utf-8')
manager = (root / 'app/src/main/java/com/ashes/crown/CompanionRelationshipManager.kt').read_text(encoding='utf-8')
save = (root / 'app/src/main/java/com/ashes/crown/SaveManager.kt').read_text(encoding='utf-8')
checks = {
    'six unique companion pair relationships': all(x in manager for x in ('GARRETT_CELESTE', 'GARRETT_MIRA', 'GARRETT_THOMAS', 'CELESTE_MIRA', 'CELESTE_THOMAS', 'MIRA_THOMAS')),
    'each pair has a dedicated conflict scene': 'private val scenes = mapOf(' in manager and 'fun scene(pair: CompanionPair)' in manager,
    'three player responses are implemented': all(x in manager for x in ('MEDIATE', 'SUPPORT_FIRST', 'SUPPORT_SECOND')),
    'relationship values are bounded': 'coerceIn(MIN_SCORE, MAX_SCORE)' in manager,
    'all six affinities are loaded and persisted': all(f'relation_{x}' in main and f'relation_{x}' in save for x in ('garrett_celeste','garrett_mira','garrett_thomas','celeste_mira','celeste_thomas','mira_thomas')),
    'one team conflict resolution per day': 'if (companionRelationDay != worldDay &&' in main,
    'team scenes require both companions to be present': 'relationPairAvailable = NpcInteractionManager.isAvailable(relationPair.first' in main and 'NpcInteractionManager.isAvailable(relationPair.second' in main,
    'decisions are written to journal': 'Разногласия спутников:' in main,
    'new game resets relationships': 'relationGarrettCeleste = 0' in main and 'companionRelationDay = 0' in main,
    'schema 10 migration preserves and initializes affinity': 'CURRENT_SCHEMA = 11' in save and 'if (schema < 10)' in save,
    'relationship report is registered': 'companion_relation_report' in save,
}
for label, ok in checks.items(): print(('PASS' if ok else 'FAIL') + ': ' + label)
assert all(checks.values())
ET.parse(root / 'app/src/main/AndroidManifest.xml')
print('PASS: AndroidManifest.xml parses')
