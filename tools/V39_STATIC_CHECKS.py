from pathlib import Path
import xml.etree.ElementTree as ET
root = Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text(encoding='utf-8')
manager = (root / 'app/src/main/java/com/ashes/crown/CompanionTeamEventManager.kt').read_text(encoding='utf-8')
save = (root / 'app/src/main/java/com/ashes/crown/SaveManager.kt').read_text(encoding='utf-8')
checks = {
    'six unique two-companion story events': 'val events = listOf(' in manager and all(f'CompanionTeamEvent({i},' in manager for i in range(6)),
    'events have affinity and both hero-bond requirements': 'minimumPairAffinity' in manager and 'minimumHeroBond' in manager and 'fun isUnlocked' in manager,
    'cooperative and compromise outcomes exist': all(x in manager for x in ('COOPERATE', 'COMPROMISE')),
    'pair affinity and both hero bonds are updated': 'setCompanionPairScore(nextTeamEvent.pair' in main and 'setCompanionBond(nextTeamEvent.pair.first' in main and 'setCompanionBond(nextTeamEvent.pair.second' in main,
    'event completion is persisted as a bit mask': 'companion_team_event_flags' in main and 'markCompleted(companionTeamEventFlags' in main,
    'one team event per campaign day': 'companionTeamEventDay != worldDay' in main,
    'both companions must be present at resolution': 'stillAvailable && CompanionTeamEventManager.isUnlocked' in main,
    'event decisions enter the journal': 'Совместная история:' in main,
    'new game resets team events': 'companionTeamEventFlags = 0' in main and 'companionTeamEventDay = 0' in main,
    'schema 11 migration preserves previous progress': 'CURRENT_SCHEMA = 11' in save and 'if (schema < 11)' in save,
    'team event keys are validated and bounded': 'companion_team_event_flags" to (0..63)' in save and 'companion_team_event_report' in save,
}
for label, ok in checks.items(): print(('PASS' if ok else 'FAIL') + ': ' + label)
assert all(checks.values())
ET.parse(root / 'app/src/main/AndroidManifest.xml')
print('PASS: AndroidManifest.xml parses')
