# V85 final audit — Part I checkpoint

## Confirmed source-level improvements in V81–V85

1. **Lifecycle persistence:** an `ON_STOP` safety save now flushes current Compose-backed campaign state to SharedPreferences. A one-shot gate prevents the outgoing Activity from overwriting a just-loaded manual slot during recreation.
2. **Manual slot completeness:** nullable `lastOutcome` is now serialized as an empty string, because Android `putString(key, null)` removes the key and schema 11 requires `outcome`. Blank is converted back to null when restoring UI state.
3. **Navigation and new-game reset:** all exposed navigation targets were checked against page renderers; all 110 `remember`-backed mutable state declarations were compared with `resetNewGame()`. Only app-level music, menu visibility and slot refresh state are intentionally retained.
4. **Resource hygiene:** 146 byte-identical duplicate concept images were removed from the nested Android resource staging directory. The 146-entry catalog and README remain in `docs/visual_assets/alpha4/`; the root drawables and their identifiers are unchanged.
5. **Slot isolation:** key filtering is centralized in `SaveSlotRules`, keeping `music_enabled` and all `slot_*` metadata outside campaign snapshots and campaign restore.

## Verification performed

- 31 pure Kotlin rule-check entry points compiled together with Kotlin language version 2.0 and all executed successfully.
- The base `RulesCheck` includes 2,000 deterministic differential day-settlement cases.
- All 56 static scripts V30–V85 were executed in batches and passed, including the new V81–V85 integration checks.
- The 146 visual catalog entries and all 150 Kotlin `R.drawable` references resolve to existing files.
- AndroidManifest XML parses successfully.
- Schema 11 remains complete against the save writer; the final static audit compares the required-key registry with keys emitted by `MainActivity.save()`.

## Explicit limitations

- No Android SDK/Gradle toolchain is available in this environment; no `:app:assembleDebug` or full Android/Compose type-check was performed.
- No APK was produced, and no emulator or physical-device test was performed.
- Lifecycle persistence and SharedPreferences commit/rollback still need runtime verification on Android. `ON_STOP` is a safety checkpoint, not a guarantee against every abrupt power loss or force-stop timing.
- The ZIP archive is a source project package, not an installable build.

## Compatibility and preservation

- `SaveManager.CURRENT_SCHEMA` remains **11**.
- Existing save keys and migrations are unchanged.
- V80 source/archive was used as the base and remains untouched.
- The original Alpha 27 archive remains untouched.
- The agreed release rule remains: no APK build/request before Part III.

## Readiness decision

V85 is the Part I **source-level stabilization checkpoint**. The pure domain tests, save contracts and resource references pass. Part I should not be labelled Android-build verified or release-ready until the project is built with its declared Android Gradle Plugin 8.7.3 / Kotlin 2.0.21 / compileSdk 35 toolchain and exercised on an emulator/device. That build-and-device gate can be scheduled at the agreed Part III stage; if a build environment becomes available earlier, it can be used for validation without distributing an APK.
