# Ashes of the Crown — V80 final audit report

## Scope

The V80 working archive is based on the user's V57 project archive. The original V57 ZIP and Alpha 27 source archive were not modified. This pass added source fixes, regression tests, static audit scripts and per-checkpoint records for V58–V80.

## Confirmed defects fixed

1. **Save schema downgrade:** removing `save_schema_version` (or setting it to JSON null) from a complete current snapshot could route it through the legacy path, which tolerated missing hashes. Current-looking schema-less snapshots are now rejected.
2. **Duplicate battle finalization:** a delayed/double victory callback could reach `winBattle()` after the encounter had already been cleared. Victory finalization now requires a nonblank enemy and exactly zero HP.
3. **Boss relic loss:** a full inventory could cause the Ancient Knight to be permanently marked defeated even though the unique relic was not received. The relic is now preflighted; if it cannot be added, the boss remains retryable and unique rewards are withheld.
4. **Quest evidence bypass:** `applyQuestDelta` previously ignored missing removal items and could complete a transition while the required item was absent. Required removal counts are now checked before any mutation.
5. **Daily gold overflow/cap mismatch:** daily province income used `Int` addition and could overflow; the balance could also cross the save cap. The interim calculation now uses `Long`, pays steward wages, then clamps the final net balance.
6. **Narrative choice item loss:** a choice could spend resources and advance the story even when its required item reward could not fit in the inventory. Choice state and item reward are now one transaction; a full bag rejects the choice without applying any effects.
7. **Daily settlement marker mismatch:** older saves without the upkeep marker now initialize it to the saved world day. Current-schema saves with inconsistent world-day/upkeep-day markers are rejected to prevent unintended backlog replay.
8. **Resource range mismatch:** narrative reputation and influence now use the same bounds as save normalization. Direct positive gold/influence rewards use shared saturating helpers.
9. **Companion arithmetic boundaries:** bond adjustments now use `Long` intermediates so extreme input values cannot wrap before clamping.

## Tests and verification

- 28 pure Kotlin rule-check entry points compiled together with Kotlin language version 2.0.
- All 28 test entry points executed successfully.
- The `RulesCheck` harness includes 2,000 deterministic differential day-settlement cases.
- Static scripts V30–V80 were retained/added and executed.
- `SaveManager.kt` and `SaveSlotStore.kt` compiled against temporary minimal Android `SharedPreferences` and `org.json` API stubs. This is a type-check, not an Android SDK build.
- `AndroidManifest.xml` parsed successfully as XML.
- `MainActivity.kt` was passed through the Kotlin parser; the compiler produced unresolved Android/Compose symbol diagnostics because the Android/Compose classpath is absent, but no parser/syntax diagnostics were found.
- ZIP integrity was checked after packaging.

## Not performed / limitations

- No APK was built.
- No full Android/Compose type-check or Gradle build was possible because the environment does not contain the Android SDK/Gradle toolchain.
- No emulator or physical-device test was performed.
- SaveSlotStore disk-failure rollback was type-checked but not runtime-tested against Android SharedPreferences.
- The checksum is an accidental-corruption check, not cryptographic authenticity protection against a user who can edit both payload and checksum.

## Compatibility and preservation

- `SaveManager.CURRENT_SCHEMA` remains **11**.
- No save key or schema migration was added for these fixes.
- The inventory's legacy delimiter format remains unchanged.
- The V57 archive remains untouched; V80 is a separate working copy/archive.
- The agreed rule remains: do not build or request APK installation before Part III is complete.

## Readiness assessment

The pure Kotlin rules and source-level persistence/gameplay contracts pass the current regression suite. This is a strong source-level stabilization checkpoint, **not a claim that the Android app is release-ready**. The next gate is to obtain a real Android build environment and run `:app:assembleDebug`, followed by emulator/device smoke tests at the agreed end of Part III.
