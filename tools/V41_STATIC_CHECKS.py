from pathlib import Path
import xml.etree.ElementTree as ET
root = Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text(encoding='utf-8')
manager = (root / 'app/src/main/java/com/ashes/crown/CrisisConsequenceManager.kt').read_text(encoding='utf-8')
checks = {
    'crisis transitions compare previous and current stages': 'previousDays' in manager and 'currentDays' in manager and 'to.ordinal <= from.ordinal' in manager,
    'faction consequences cover crown, merchants, people and rose': all(x in manager for x in ('crownDelta', 'merchantDelta', 'peopleDelta', 'roseDelta')),
    'regional spillover uses explicit province adjacency': 'private val neighbors' in manager and 'affectedNeighbors' in manager,
    'effects are bounded through faction manager at integration': 'FactionManager.applyDelta(crownRep, transition.crownDelta)' in main and 'FactionManager.applyDelta(merchantRep, transition.merchantDelta)' in main and 'FactionManager.applyDelta(peopleRep, transition.peopleDelta)' in main and 'FactionManager.applyDelta(roseRep, transition.roseDelta)' in main,
    'regional loyalty effect is applied to adjacent provinces': 'transition.neighboringLoyaltyLoss' in main and 'affectedNeighbors' in main,
    'transition outcomes are recorded in journal and daily report': 'Политические последствия · день' in main and 'worldEventNotes += transition.message' in main,
    'no new persistence keys or schema migration added': 'CURRENT_SCHEMA = 11' in (root / 'app/src/main/java/com/ashes/crown/SaveManager.kt').read_text(encoding='utf-8'),
}
for label, ok in checks.items(): print(('PASS' if ok else 'FAIL') + ': ' + label)
assert all(checks.values())
ET.parse(root / 'app/src/main/AndroidManifest.xml')
print('PASS: AndroidManifest.xml parses')
