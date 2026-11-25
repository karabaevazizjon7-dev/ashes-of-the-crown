from pathlib import Path
import re, xml.etree.ElementTree as ET
root = Path(__file__).resolve().parents[1]
src = root / 'app/src/main/java/com/ashes/crown'
main = (src / 'MainActivity.kt').read_text(encoding='utf-8')
save = (src / 'SaveManager.kt').read_text(encoding='utf-8')
slot = (src / 'SaveSlotStore.kt').read_text(encoding='utf-8')

# The literal key registry must be fully emitted by the current save() writer.
save_body = main[main.index('    fun save() {'):main.index('    val saveSlotStore =')]
written = set(re.findall(r'\.put(?:Int|String|Boolean|Long|Float)\("([^"]+)"', save_body))
required_body = save.split('internal val currentSchemaRequiredKeys = setOf(', 1)[1].split('\n    )', 1)[0]
required = set(re.findall(r'"([^"]+)"', required_body))
missing = sorted(required - written)
assert not missing, f'Current schema registry contains keys not emitted by MainActivity.save(): {missing}'

# Current schema, checksum gate and slot application ordering remain explicit.
assert 'const val CURRENT_SCHEMA = 11' in save
load_body = slot[slot.index('fun loadSlot'):slot.index('private fun putValue')]
assert load_body.index('SaveManager.validateSnapshot(rawValues, maxSceneIndex)') < load_body.index('preferences.edit().clear()')
assert 'SaveManager.requiresChecksum(accepted.sourceSchema)' in load_body
assert 'if (!editor.commit())' in load_body and 'restoreAll(previousPreferences)' in load_body

# New V81–V84 contracts.
assert 'SaveLifecycleGate' in main and 'event == Lifecycle.Event.ON_STOP) latestLifecycleSave()' in main
assert 'SaveDataRules.encodeOptionalText(lastOutcome)' in main
assert 'SaveDataRules.decodeOptionalText(prefs.getString("outcome", null))' in main
assert 'SaveSlotRules.includeInCampaignSnapshot(key)' in slot
assert 'SaveSlotRules.restoreIntoCampaign(key)' in slot

# All Kotlin drawable references and manifest XML are structurally valid.
drawables = root / 'app/src/main/res/drawable-nodpi'
available = {p.stem for p in drawables.iterdir() if p.is_file()}
refs = set()
for file in (root / 'app/src/main/java').rglob('*.kt'):
    refs.update(re.findall(r'R\.drawable\.([A-Za-z0-9_]+)', file.read_text(encoding='utf-8')))
assert not (refs - available), f'Missing drawable references: {sorted(refs - available)}'
ET.parse(root / 'app/src/main/AndroidManifest.xml')

for version in range(81, 86):
    assert (root / f'VERSION_{version}_CHANGELOG.md').is_file(), f'Missing V{version} changelog'
    assert (root / f'VERSION_{version}_AUDIT.md').is_file(), f'Missing V{version} audit record'

print(f'PASS: {len(required)} schema-11 required keys are emitted by save()')
print(f'PASS: {len(refs)} Kotlin drawable references resolve; Manifest XML parses')
print('PASS: save checksum, validation-before-clear, rollback, lifecycle and slot isolation contracts')
print('V85 static checks: PASS')
