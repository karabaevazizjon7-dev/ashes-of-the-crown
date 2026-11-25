# V124 — Save Recovery & Migration Verification

Base: V123. Previous archives and source directory are preserved unchanged.

## Changes
- Added `tools/SaveRecoveryRulesCheck.kt` with regression coverage for current schema 11, schema 10 migration, schema 7 companion-bond migration, day-settlement marker consistency, SHA-256 verification, legacy checksum compatibility, and stale Activity lifecycle-save suppression.
- Added `tools/V124_SAVE_RECOVERY_AUDIT.py` to statically verify the save-integrity boundary and regression coverage.
- No save schema bump. `SaveManager.CURRENT_SCHEMA` remains 11.
- No game-content, balance, or APK packaging changes.

## Compatibility decision
`SaveManager.checksumMatches` intentionally continues to treat a missing checksum as legacy-compatible at its pure helper boundary. `SaveSlotStore.loadSlot` separately enforces the stricter rule: current-schema slots must contain a checksum, while genuinely older schemas may omit it. This preserves the existing legacy behavior tested by `RulesCheck.kt`.

## Verification
- `python3 tools/PART3_DOMAIN_TESTS.py`: 36/36 suites PASS.
- `python3 tools/V124_SAVE_RECOVERY_AUDIT.py`: 8/8 PASS.
- Python bytecode syntax check for the new audit: PASS.
- Full Android/Compose Gradle build and Android Lint: not run in this environment; no claim of Android build success.
- APK: not produced, per Part III release policy.
