from pathlib import Path
import xml.etree.ElementTree as ET
root = Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text()
manager = (root / 'app/src/main/java/com/ashes/crown/RandomEncounterManager.kt').read_text()
assert 'RandomEncounterManager.resolve(route, departureDay, departureHour, encounterCount)' in main
assert 'TravelEncounter.Ambush' in main and 'beginBattle(event.enemyName, event.enemyHealth)' in main
assert 'TravelEncounter.Merchant' in main and 'addInventoryItem(event.itemReward)' in main
assert 'TravelEncounter.Traveler' in main
assert 'CURRENT_SCHEMA' in (root / 'app/src/main/java/com/ashes/crown/SaveManager.kt').read_text()
assert 'Math.floorMod' in manager and 'dangerousSegments' in manager
ET.parse(root / 'app/src/main/AndroidManifest.xml')
print('v30 integration references and AndroidManifest XML checks passed')
