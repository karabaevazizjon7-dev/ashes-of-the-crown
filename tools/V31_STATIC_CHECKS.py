from pathlib import Path
import xml.etree.ElementTree as ET
root = Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text()
npc = (root / 'app/src/main/java/com/ashes/crown/NpcScheduleManager.kt').read_text()
factions = (root / 'app/src/main/java/com/ashes/crown/FactionManager.kt').read_text()
assert 'NpcScheduleManager.report(currentLocation, worldHour)' in main
assert 'FactionManager.snapshot(crownRep, merchantRep, peopleRep, roseRep)' in main
assert 'npcSchedule.description' in main
assert 'factionSnapshot.standings.forEach' in main
assert 'val npcSchedule = when (currentLocation)' not in main
assert 'fun applyDelta' in factions and 'coerceIn' in factions
assert 'Math.floorMod(hour, 24)' in npc
assert 'CURRENT_SCHEMA = 11' in (root / 'app/src/main/java/com/ashes/crown/SaveManager.kt').read_text()
assert 'NpcScheduleManager' in npc and 'FactionManager' in factions
ET.parse(root / 'app/src/main/AndroidManifest.xml')
print('v31 NPC/faction integration references and AndroidManifest XML checks passed')
