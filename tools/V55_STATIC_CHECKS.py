from pathlib import Path
import re

root = Path(__file__).resolve().parents[1]
save = (root / "app/src/main/java/com/ashes/crown/SaveManager.kt").read_text(encoding="utf-8")
main = (root / "app/src/main/java/com/ashes/crown/MainActivity.kt").read_text(encoding="utf-8")
test = (root / "tools/SaveSnapshotCompletenessRulesCheck.kt").read_text(encoding="utf-8")
save_body = main[main.index("fun save()"):main.index("val saveSlotStore")]
written = set(re.findall(r'\.put(?:Int|String|Boolean|Long|Float)\("([^"]+)"', save_body))
written = {key for key in written if "$" not in key}
registry_match = re.search(
    r"internal val currentSchemaRequiredKeys = setOf\((.*?)\n    \)",
    save,
    re.S,
)
assert registry_match, "current-schema required-key registry missing"
registered = set(re.findall(r'"([^"]+)"', registry_match.group(1)))
checks = {
    "current schema registry exactly matches fixed keys written by MainActivity.save()": written == registered,
    "current schema completeness check runs before accepting snapshot": "if (schema >= CURRENT_SCHEMA)" in save and "currentSchemaRequiredKeys.firstOrNull" in save,
    "partial current-schema saves are explicitly rejected": "Сохранение текущей версии неполное" in save,
    "legacy schema 10 compatibility remains covered": '"save_schema_version" to 10' in test and "is SaveValidationResult.Accepted" in test,
    "test removes every fixed current-schema field": "requiredWithoutSchema.forEach" in test and "missingKey" in test,
    "dynamic per-NPC dialogue keys remain optional": '"dialog_stage_$dialogNpc"' in save_body and '"dialog_trust_$dialogNpc"' in save_body and '"dialog_stage_$dialogNpc"' not in registry_match.group(1),
    "save schema remains 11": "const val CURRENT_SCHEMA = 11" in save,
}
for label, ok in checks.items():
    print(("PASS" if ok else "FAIL") + ": " + label)
print(f"Current fixed save fields: {len(registered)}")
assert all(checks.values())
