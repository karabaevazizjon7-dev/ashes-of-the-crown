from pathlib import Path
import re
import xml.etree.ElementTree as ET

root = Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text(encoding='utf-8')
manager = (root / 'app/src/main/java/com/ashes/crown/NpcInteractionManager.kt').read_text(encoding='utf-8')
schedule = (root / 'app/src/main/java/com/ashes/crown/NpcScheduleManager.kt').read_text(encoding='utf-8')
save = (root / 'app/src/main/java/com/ashes/crown/SaveManager.kt').read_text(encoding='utf-8')
checks = {
    'interaction manager delegates to the current time-derived NPC roster': 'NpcScheduleManager.roster(hour)' in manager,
    'player and NPC locations are canonicalized before comparison': 'WorldManager.canonicalName(playerLocation)' in manager and 'WorldManager.canonicalName(status.location)' in manager,
    'away NPCs return a typed unavailable result': 'NpcInteractionResult.Away' in manager,
    'busy NPCs return a typed unavailable result': 'NpcInteractionResult.Busy' in manager,
    'legacy unscheduled story character has a castle fallback': 'expectedLocation = CASTLE' in manager,
    'dialogue page evaluates the selected NPC interaction': 'NpcInteractionManager.evaluate(dialogNpc, currentLocation, worldHour)' in main,
    'dialogue topic buttons are disabled when NPC is unavailable': 'enabled = dialogStage < 3 && npcCanTalk' in main,
    'dialogue click handler independently guards unavailable NPCs': 'if (!NpcInteractionManager.isAvailable(dialogNpc, currentLocation, worldHour))' in main,
    'dialogue page explains why interaction is unavailable': 'Личная беседа недоступна' in main,
    'existing NPC schedule remains the source of availability': 'availableForConversation' in schedule,
    'save schema is version 8 after companion migration': 'CURRENT_SCHEMA = 11' in save,
    'no new NPC-interaction preference key was introduced': not re.search(r'put(?:Int|String|Boolean)\("npc_interaction', main),
}
for label, passed in checks.items():
    print(('PASS' if passed else 'FAIL') + ': ' + label)
assert all(checks.values())
ET.parse(root / 'app/src/main/AndroidManifest.xml')
print('AndroidManifest.xml parsed successfully')
