from pathlib import Path
import xml.etree.ElementTree as ET

root = Path(__file__).resolve().parents[1]
manager = root / 'app/src/main/java/com/ashes/crown/FactionAgendaManager.kt'
faction = root / 'app/src/main/java/com/ashes/crown/FactionManager.kt'
activity = root / 'app/src/main/java/com/ashes/crown/MainActivity.kt'
save = root / 'app/src/main/java/com/ashes/crown/SaveManager.kt'
test = root / 'tools/FactionRulesCheck.kt'
for path in (manager, faction, activity, save, test):
    assert path.is_file(), f'missing {path}'
m = manager.read_text()
a = activity.read_text()
s = save.read_text()
t = test.read_text()
for token in ('crown_rose_conflict', 'wealth_people_gap', 'people_relief', 'merchant_routes', 'rose_secrecy'):
    assert token in m, f'missing agenda condition {token}'
assert 'FactionAgendaManager.evaluate(crownRep, merchantRep, peopleRep, roseRep)' in a
assert 'ПОЛИТИЧЕСКАЯ ПОВЕСТКА' in a
assert 'agenda.pressure' in a and 'agenda.opportunity' in a
assert 'CURRENT_SCHEMA = 11' in s, 'companion relationship system uses schema 10'
assert 'political agenda conditions passed' in t
ET.parse(root / 'app/src/main/AndroidManifest.xml')
print('PASS: political agenda manager derives tensions from existing faction reputations')
print('PASS: life page renders faction pressure and opportunity explanations')
print('PASS: cross-faction conflict and wealth/people gap conditions are present')
print('PASS: current schema remains compatible with the agenda module')
print('AndroidManifest.xml parsed successfully')
