# Working version 21 — save-slot storage and validation hardening

## Manual save slots
- Added `SaveSlotStore.kt` to isolate the Android SharedPreferences and JSON boundary for manual save slots.
- Moved slot JSON serialization/parsing, checksum checks, slot metadata preservation and final preference commits out of `MainActivity.kt`.
- Save-slot snapshots are validated and normalized before serialization.
- A failed slot write attempts to restore the previous slot data.
- Loading validates the entire payload before replacing active campaign preferences; music preference and all three slot payloads/labels/checksums are preserved.
- A failed active-preference commit attempts to restore the previous preference image.

## Save validation
- Integer campaign fields, schema versions and per-character dialogue counters now reject fractional and non-finite numeric values instead of truncating them.
- Added regression checks for fractional schema markers, fractional currency values and NaN.
- Campaign save schema remains version 7; no new gameplay keys were introduced.

## Architecture
- `MainActivity.kt` delegates manual slot operations to `SaveSlotStore` and keeps only UI feedback and Activity recreation.
- `SaveManager.kt` remains the pure validation/normalization/integrity rules component.

## Verification performed
- Compiled `GameRules.kt`, `BattleRules.kt`, `RealmDayProcessor.kt`, `SaveDataRules.kt`, `SaveManager.kt` and `RulesCheck.kt` with the installed Kotlin compiler.
- Executed the pure Kotlin regression harness successfully, including the 2,000 deterministic realm-day differential cases and the new malformed-number checks.
- Type-checked `SaveSlotStore.kt` against minimal compile-time stubs for the Android SharedPreferences and JSON APIs. Real Android SDK compilation was not available in this environment.
- ZIP structure and source-level references were checked.

## Not performed
- No Android Gradle build, APK assembly, emulator run or physical-device test.
- The save-slot adapter's real Android integration remains to be verified in the final Part III build/test pass.
