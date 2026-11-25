from pathlib import Path
import xml.etree.ElementTree as ET
root = Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text()
manager = (root / 'app/src/main/java/com/ashes/crown/WorldEventsManager.kt').read_text()
checks = [
    'WorldEventsManager.resolve(' in main,
    'WorldEventContext(' in main,
    'dailyWorldEvent.crownReputationDelta' in main,
    'dailyWorldEvent.merchantReputationDelta' in main,
    'dailyWorldEvent.peopleReputationDelta' in main,
    'dailyWorldEvent.roseReputationDelta' in main,
    'dailyWorldEvent.provinceLoyaltyDelta' in main,
    'worldEventNotes.last()' in main,
    'fun resolve(context: WorldEventContext)' in manager,
    'Math.floorMod(seed, candidates.size.toLong())' in manager,
]
assert all(checks), checks
save_manager = (root / 'app/src/main/java/com/ashes/crown/SaveManager.kt').read_text()
assert 'CURRENT_SCHEMA = 11' in save_manager
# New events are derived from the existing daily-upkeep marker; no new preference key is introduced.
assert 'world_event_day' not in main and 'world_event_id' not in main
ET.parse(root / 'app/src/main/AndroidManifest.xml')
print('v32 daily world-event integration, existing save-schema boundary and AndroidManifest XML checks passed')
