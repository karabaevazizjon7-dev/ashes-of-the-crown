# Working version 22 — quest progression and domain rules

## Added
- `app/src/main/java/com/ashes/crown/QuestManager.kt`: pure Kotlin quest rules and explicit medicine-quest transitions.
- `tools/QuestRulesCheck.kt`: deterministic regression checks for the quest state machine and story gates.

## Changed
- Market quest acceptance/completion now uses one transition result applied through a single state-update function.
- Port crate discovery now uses the same quest state machine and cannot duplicate the crate.
- Completing the medicine quest consumes the crate, grants the established 8-gold / +4 commoner-reputation reward, and awards the empty cargo tag once.
- Black Rose evidence, old chapel, Seven Houses entry, alliance selection and alliance finalization preconditions are centralized in `QuestManager` and used by the UI callbacks.
- Save schema remains version 7; no new persisted keys were added.

## Verification
- `QuestManager.kt` + `QuestRulesCheck.kt`: compiled and all assertions passed.
- Existing pure Kotlin rules harness: compiled and passed, including 2,000 realm-day differential cases.
- Modified `MainActivity.kt` was parser-checked with `kotlinc`; no syntax/parser diagnostics were reported. Android/Compose symbol resolution was not available in this check.
- No Android/Gradle build, APK, emulator or device test was run.
