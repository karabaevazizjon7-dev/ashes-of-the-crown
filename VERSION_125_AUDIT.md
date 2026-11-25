# V125 — Verification Report

## Scope
Cross-system domain integration regression coverage. Production gameplay files and persistence schema were intentionally not changed.

## Executed checks
- `python3 tools/PART3_DOMAIN_TESTS.py` — **37/37 test mains PASS**. Kotlin compiler compiled 36 platform-independent source files and 37 test files. Four compiler warnings were reported as non-fatal unnecessary-cast warnings.
- `python3 tools/V125_CAMPAIGN_LOOP_AUDIT.py` — **8/8 PASS**.
- `python3 tools/V124_SAVE_RECOVERY_AUDIT.py` — **8/8 PASS**.
- AndroidManifest XML parse — **PASS**.

## Integration scenario exercised
1. Resolve a narrative choice and atomically apply its gold, reputation, and item effects.
2. Accept the medicine quest, find its crate, add the evidence item, complete the quest, consume the crate, and retain the reward item.
3. Resolve a companion-to-companion mediation scene and preserve the resulting affinity.
4. Find and validate a route from the royal castle to Port Weimar.
5. Resolve a deterministic combat victory and verify its reward plan.
6. Construct and validate a complete schema-11 campaign snapshot, then verify restored values.
7. Corrupt the daily-settlement marker and verify that snapshot validation rejects it.

## Explicit limitations
- This is a deterministic domain integration scenario, not an Android UI automation run or a real device playthrough.
- Android/Compose Gradle build and Android Lint were not run in this environment.
- No APK was generated.
- Save schema remains 11.
