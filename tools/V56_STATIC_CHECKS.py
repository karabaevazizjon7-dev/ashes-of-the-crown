from pathlib import Path
import re

root = Path(__file__).resolve().parents[1]
save = (root / "app/src/main/java/com/ashes/crown/SaveManager.kt").read_text(encoding="utf-8")
slot = (root / "app/src/main/java/com/ashes/crown/SaveSlotStore.kt").read_text(encoding="utf-8")
slot_rules = (root / "app/src/main/java/com/ashes/crown/SaveSlotRules.kt").read_text(encoding="utf-8")
test = (root / "tools/SaveSnapshotCompletenessRulesCheck.kt").read_text(encoding="utf-8")

load_body = slot[slot.index("fun loadSlot"):slot.index("private fun putValue")]
checks = {
    "current schema requires checksum": "fun requiresChecksum(sourceSchema: Int): Boolean = sourceSchema >= CURRENT_SCHEMA" in save,
    "snapshot is parsed and validated before current-schema checksum policy": load_body.index("JSONObject(raw)") < load_body.index("SaveManager.validateSnapshot(rawValues, maxSceneIndex)") < load_body.index("SaveManager.requiresChecksum(accepted.sourceSchema)"),
    "missing current-schema checksum is rejected without changing active preferences": "У сохранения текущей версии отсутствует контрольная сумма" in load_body and load_body.index("SaveManager.requiresChecksum(accepted.sourceSchema)") < load_body.index("preferences.edit().clear()"),
    "present checksum is still verified": "!SaveManager.checksumMatches(raw, expectedChecksum)" in load_body,
    "legacy schema without checksum remains supported": "check(!SaveManager.requiresChecksum(10))" in test,
    "current schema checksum policy has regression coverage": "check(SaveManager.requiresChecksum(SaveManager.CURRENT_SCHEMA))" in test,
    "future schema cannot bypass checksum policy": "check(SaveManager.requiresChecksum(SaveManager.CURRENT_SCHEMA + 1))" in test,
    "slot metadata and music are preserved by the shared key policy": 'const val MUSIC_KEY = "music_enabled"' in slot_rules and 'SLOT_RANGE.forEach' in load_body and 'SaveSlotRules.restoreIntoCampaign(key)' in load_body,
    "save schema remains 11": "const val CURRENT_SCHEMA = 11" in save,
}
for label, ok in checks.items():
    print(("PASS" if ok else "FAIL") + ": " + label)
assert all(checks.values())
