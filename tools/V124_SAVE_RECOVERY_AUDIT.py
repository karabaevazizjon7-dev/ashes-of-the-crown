#!/usr/bin/env python3
"""Static guardrails for the V124 save-integrity regression suite."""
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
manager = (ROOT / "app/src/main/java/com/ashes/crown/SaveManager.kt").read_text(encoding="utf-8")
tests = (ROOT / "tools/SaveRecoveryRulesCheck.kt").read_text(encoding="utf-8")
store = (ROOT / "app/src/main/java/com/ashes/crown/SaveSlotStore.kt").read_text(encoding="utf-8")
checks = {
    "legacy checksum compatibility is isolated from current-schema slot enforcement": "SaveManager.requiresChecksum(accepted.sourceSchema)" in store and "expectedChecksum.isNullOrBlank()" in store,
    "SHA-256 verification remains case-insensitive": "equals(expected, ignoreCase = true)" in manager,
    "legacy schema 7 migration is covered": 'put("save_schema_version", 7)' in tests,
    "legacy schema 10 migration is covered": 'put("save_schema_version", 10)' in tests,
    "schema 11 day marker mismatch is rejected": 'put("a14_upkeep_day", 999)' in tests,
    "checksum tampering is covered": 'hash.dropLast(1)' in tests,
    "stale lifecycle save suppression is covered": "suppressNextStopSave" in tests,
    "slot load validates before campaign commit": store.index("SaveManager.validateSnapshot(rawValues") < store.index("editor.commit()"),
}
for name, ok in checks.items():
    print(("PASS" if ok else "FAIL") + " — " + name)
print(f"V124_SAVE_RECOVERY_AUDIT: {sum(checks.values())}/{len(checks)} PASS")
raise SystemExit(0 if all(checks.values()) else 1)
