from pathlib import Path
import xml.etree.ElementTree as ET
root = Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text(encoding='utf-8')
story = (root / 'app/src/main/java/com/ashes/crown/CompanionStoryManager.kt').read_text(encoding='utf-8')
save = (root / 'app/src/main/java/com/ashes/crown/SaveManager.kt').read_text(encoding='utf-8')
checks = {
    'four distinct three-chapter personal story arcs': all(n in story for n in ('Гаррет Морн', 'Селеста', 'Мира', 'Томас')) and 'LAST_STAGE = 3' in story,
    'story choices have explicit outcomes': all(x in story for x in ('CompanionStoryChoice.SUPPORT', 'CompanionStoryChoice.INVESTIGATE')),
    'trust gates are enforced in manager and UI': 'require(CompanionManager.normalizeBond(currentBond) >= chapter.minimumBond)' in story and 'selectedCompanionBond() >= storyChapter.minimumBond' in main,
    'each companion story stage is loaded and saved': all(x in main and x in save for x in ('story_companion_garrett', 'story_companion_celeste', 'story_companion_mira', 'story_companion_thomas')),
    'story progress is journaled': 'Личная история ${result.companion}' in main,
    'story choices require companion availability': 'NpcInteractionManager.isAvailable(companion, currentLocation, worldHour)' in main,
    'schema 9 migration initializes legacy story progress': 'CURRENT_SCHEMA = 11' in save and 'if (schema < 9)' in save,
    'story report is registered': 'companion_story_report' in save,
    'new game resets story progress': 'storyStageGarrett = 0' in main and 'storyStageThomas = 0' in main,
}
for label, ok in checks.items(): print(('PASS' if ok else 'FAIL') + ': ' + label)
assert all(checks.values())
manifest = root / 'app/src/main/AndroidManifest.xml'
ET.parse(manifest)
print('PASS: AndroidManifest.xml parses')
