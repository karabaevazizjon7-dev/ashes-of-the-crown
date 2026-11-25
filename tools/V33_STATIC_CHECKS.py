from pathlib import Path
import re
import xml.etree.ElementTree as ET

root = Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text()
npc = (root / 'app/src/main/java/com/ashes/crown/NpcScheduleManager.kt').read_text()
save = (root / 'app/src/main/java/com/ashes/crown/SaveManager.kt').read_text()
checks = {
    'life page reads the current-location NPC report': 'NpcScheduleManager.report(currentLocation, worldHour)' in main,
    'life page renders the time-derived NPC roster': 'NpcScheduleManager.roster(worldHour)' in main,
    'life page shows next schedule transition': 'NpcScheduleManager.hoursUntilNextChange(worldHour)' in main,
    'NPC schedule uses canonical world locations': 'WorldManager.canonicalName(location)' in npc,
    'NPC roster derives locations from campaign hour': 'fun roster(hour: Int)' in npc,
    'NPC transition finder checks future timetable changes': 'fun hoursUntilNextChange(hour: Int)' in npc,
    'four named characters have timetable segments': all(name in npc for name in ('Гаррет Морн', 'Селеста', 'Мира', 'Томас')),
    'save schema advances for per-companion relationship migration': 'CURRENT_SCHEMA = 11' in save,
    'no new NPC schedule preference key was introduced': not re.search(r'put(?:Int|String|Boolean)\("npc_', main),
}
for label, passed in checks.items():
    print(('PASS' if passed else 'FAIL') + ': ' + label)
assert all(checks.values())
ET.parse(root / 'app/src/main/AndroidManifest.xml')
print('AndroidManifest.xml parsed successfully')
