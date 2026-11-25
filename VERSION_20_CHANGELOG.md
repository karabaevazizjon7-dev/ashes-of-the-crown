# Working version 20 — global foundation update

## Save and persistence
- Extracted save snapshot validation and normalization into `SaveManager.kt`.
- Added SHA-256 integrity checks to new manual save slots.
- Preserved compatibility for legacy slots that do not have a checksum.
- Kept active campaign state untouched when JSON parsing, checksum verification or schema validation fails.
- Preserved the three save slots and the music preference when loading a campaign.
- Kept campaign save schema version 7; no gameplay state fields were added.

## Architecture
- `MainActivity.kt` no longer owns the long list of save schema/type/range validation rules.
- `SaveManager.kt` has no Android or Compose dependencies.
- SharedPreferences storage and Activity recreation are intentionally still at the Android boundary and remain candidates for the next extraction.

## Regression checks
- Added save validation, migration-default, range-clamping and checksum tests to `tools/RulesCheck.kt`.
- Compiled the pure Kotlin modules with the installed Kotlin compiler and ran the checks successfully, including 2,000 realm-day differential cases.

## Not performed
- No Android Gradle build, APK assembly, emulator launch or physical-device test.

## Build pipeline documentation
- Corrected the GitHub Actions workflow to build the checked-out project directly from the repository root instead of concatenating missing binary parts and entering a nonexistent `ashes_v2` directory.
- Added explicit root-layout checks and cancellation of superseded builds on the same ref.
- Updated README instructions to open the actual project root and use the current workflow artifact name.
- The workflow has been statically inspected but was not executed in this environment.
