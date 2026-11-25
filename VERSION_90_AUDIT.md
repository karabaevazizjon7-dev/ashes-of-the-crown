# V90 technical audit

## Result

V90 is a responsive layout update for the full-width atlas, inventory and relationship pages. It does not alter gameplay rules or save data.

## Files changed

- `app/src/main/java/com/ashes/crown/MainActivity.kt`
- `app/src/main/java/com/ashes/crown/CollectionLayoutRules.kt` (new)
- `tools/CollectionLayoutRulesCheck.kt` (new)
- `tools/V90_STATIC_CHECKS.py` (new)
- `tools/V82_STATIC_CHECKS.py` (updated to the current navigation rail)
- `VERSION_90_CHANGELOG.md`
- `VERSION_90_AUDIT.md`
- `PART_II_PROGRESS.md`

## Layout behavior

- Inventory: one column below 560 dp; two columns at and above 560 dp.
- Relationship cards: same breakpoint; portrait dimensions reduced to 58 × 68 dp.
- Map: paired map nodes use equal row weights and no longer cap their width at 155 dp.
- Existing full-width page routing remains active for map, inventory, people, companions and dialogues.

## Test evidence

- 33 Kotlin rule-test suites compiled together with the pure Kotlin game modules and passed.
- `CollectionLayoutRulesCheck`: 202 assertions passed.
- Static scripts V30–V90 passed. V82's older check was updated because it expected a retired navigation rail; it now checks the current primary navigation targets and new-game reset coverage.
- 159 `R.drawable` references in `MainActivity.kt` resolve to packaged resources.
- AndroidManifest XML parsed successfully.
- MainActivity delimiter scan passed.

## Not tested

- No full Android/Jetpack Compose build: Android SDK/Gradle toolchain is unavailable here.
- No emulator or physical-device screenshot test. The 560 dp breakpoint is a responsive layout rule, not a claim that every phone renders in two columns.
- No audio assets or save schema changes were made.
