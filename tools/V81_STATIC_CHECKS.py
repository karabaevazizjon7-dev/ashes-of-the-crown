from pathlib import Path
root = Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text(encoding='utf-8')
gate = (root / 'app/src/main/java/com/ashes/crown/SaveLifecycleGate.kt').read_text(encoding='utf-8')
save_data = (root / 'app/src/main/java/com/ashes/crown/SaveDataRules.kt').read_text(encoding='utf-8')
save_manager = (root / 'app/src/main/java/com/ashes/crown/SaveManager.kt').read_text(encoding='utf-8')
checks = {
    'nullable outcome is serialized through the tested encoder': '.putString("outcome", SaveDataRules.encodeOptionalText(lastOutcome))' in main and 'fun encodeOptionalText(value: String?): String = value.orEmpty()' in save_data,
    'empty serialized outcome restores to null UI state': 'SaveDataRules.decodeOptionalText(prefs.getString("outcome", null))' in main and 'fun decodeOptionalText(value: String?): String? = value?.takeIf { it.isNotBlank() }' in save_data,
    'manual slot load suppresses outgoing Activity save': 'saveLifecycleGate.suppressNextStopSave()' in main,
    'lifecycle stop triggers latest save callback': 'event == Lifecycle.Event.ON_STOP) latestLifecycleSave()' in main,
    'schema 11 requires outcome field': '"outcome"' in save_manager.split('internal val currentSchemaRequiredKeys = setOf(', 1)[1].split('\n    )', 1)[0],
    'lifecycle gate is one-shot': 'skipNextStopSave = false' in gate and 'skipNextStopSave = true' in gate,
    'lifecycle gate regression test exists': (root / 'tools/SaveLifecycleGateRulesCheck.kt').exists(),
    'optional save text has executable regression coverage': (root / 'tools/OptionalSaveTextRulesCheck.kt').exists(),
}
for label, ok in checks.items():
    print(('PASS' if ok else 'FAIL') + ': ' + label)
    assert ok, label
print(f'V81 static checks: {len(checks)} passed')
