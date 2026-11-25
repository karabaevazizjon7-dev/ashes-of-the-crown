from pathlib import Path
root = Path(__file__).resolve().parents[1]
store = (root / 'app/src/main/java/com/ashes/crown/SaveSlotStore.kt').read_text(encoding='utf-8')
rules = (root / 'app/src/main/java/com/ashes/crown/SaveSlotRules.kt').read_text(encoding='utf-8')
test = (root / 'tools/SaveSlotRulesCheck.kt').read_text(encoding='utf-8')
checks = {
    'slot snapshot uses shared key policy': 'SaveSlotRules.includeInCampaignSnapshot(key)' in store,
    'slot restore uses shared key policy': 'SaveSlotRules.restoreIntoCampaign(key)' in store,
    'music preference is excluded from campaign snapshots': 'key != MUSIC_KEY' in rules,
    'manual slot metadata is excluded by prefix': '!key.startsWith(SLOT_PREFIX)' in rules,
    'three slots are still range-limited': 'val SLOT_RANGE = 1..3' in store,
    'slot key policy regression test exists': 'SaveSlotRulesCheck' in test,
    'slot load validates before clearing active preferences': store.index('SaveManager.validateSnapshot(rawValues, maxSceneIndex)') < store.index('preferences.edit().clear()'),
    'failed commit triggers rollback': 'if (!editor.commit())' in store and 'restoreAll(previousPreferences)' in store,
}
for label, ok in checks.items():
    print(('PASS' if ok else 'FAIL') + ': ' + label)
    assert ok, label
print(f'V84 static checks: {len(checks)} passed')
