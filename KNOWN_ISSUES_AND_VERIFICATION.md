# Known issues and verification status — working version 19

This file separates completed source changes from work that still requires implementation or final device verification. It is not a claim that every possible defect has been found.

## Confirmed work completed in source

- Save-slot JSON is parsed and checked before replacing active preferences; core keys are required and unsupported nested values are rejected.
- Province arrays are parsed position-by-position and bounded to their legal ranges.
- Daily army/province settlement is deterministic and separately testable.
- Battle reward planning and combat calculations are in pure Kotlin modules.
- In-app version labels and the README's top-level project identity now match the Gradle baseline Alpha 27.
- The main menu has a persistent music toggle; combat music now has explicit start and exit events.
- Saving a manual slot refreshes its visible label immediately and displays a confirmation.

## Remaining implementation work

1. `MainActivity.kt` remains a very large file and still owns most UI state, save/load orchestration, and action callbacks. Further extraction into `SaveManager`, `GameState`, `BattleManager`, `QuestManager` and `EconomyManager` is still required.
2. The settings experience is still minimal: the main menu has a music toggle, but there is no dedicated settings page, volume control, text-size option or accessibility settings.
3. Inventory is still primarily a list of strings. A typed item/equipment model, item categories, stack rules, equipment slots, item use and consistent shop flows remain future work.
4. Story and side-quest paths need full manual playthroughs to verify every gate, branch, reward, failure state and ending. Static checks cannot establish narrative balance or softlock-free progression.
5. The Android project has not been compiled in this pass. The installed standalone Kotlin checks cover only the pure rules/domain modules, not Compose or Android integration.
6. The repository still needs a Gradle Wrapper and a corrected CI workflow path before reliable repeatable Android builds can be run.
7. Audio lifecycle, responsive layouts on real devices, backup/restore, process death, slot migration and long-session performance still need device/emulator verification.

## Verification performed in working version 19

- Compiled `GameRules.kt`, `BattleRules.kt`, `SaveDataRules.kt`, `RealmDayProcessor.kt` and `tools/RulesCheck.kt` with the installed Kotlin compiler.
- Executed deterministic assertions for combat calculations, reward limits, malformed save-list parsing, daily-marker normalization, army provisioning, shortages, province crises, revenue and steward payroll.
- Ran 2,000 deterministic differential cases comparing the extracted realm-day processor with the prior inline algorithm for valid game-state inputs.
- Inspected the changed Kotlin source for the new call sites and verified the archive structure after packaging.

## Verification deliberately deferred

- No Android Gradle build, APK assembly, emulator launch or physical-device test was run. As agreed, the full integrated verification remains scheduled for the end of Part III.

## Version 20 additions

- Save validation and normalization are now isolated in `SaveManager.kt`; SharedPreferences I/O and Activity recreation are still performed by `MainActivity.kt`.
- New manual slots store a SHA-256 checksum. Legacy slots without checksums are accepted; a present but mismatching checksum blocks loading without replacing the current campaign.
- Pure Kotlin tests cover malformed saves, missing required fields, future schemas, clamping, legacy defaults and checksum mismatch.
- The Android app and Compose integration remain uncompiled and untested in this pass. This is deliberate under the agreed schedule; final integrated build/device verification remains for Part III.
- The GitHub Actions workflow path/layout mismatch is corrected in working version 20, but the workflow itself has not been executed; a green CI run is still required to confirm the Android build.


## Version 22 additions

- Added a pure Kotlin `QuestManager` and routed medicine-crate quest transitions and major Black Rose / Seven Houses progression gates through it. The quest uses existing persisted fields; schema remains version 7.
- Added `tools/QuestRulesCheck.kt`; it compiles and passes the medicine quest state-machine and story-gate regression assertions.
- Re-ran the existing pure Kotlin rules harness successfully, including the 2,000 deterministic realm-day differential cases.
- Parser-only Kotlin check found no syntax diagnostics in the changed `MainActivity.kt`; this does not resolve Android/Compose types and is not a project compilation.
- Remaining: full Android build, save-slot integration against real Android APIs, manual quest playthroughs, process-death/device tests, typed inventory, dedicated settings UI, and further extraction of battle/economy/state/action orchestration.

## Version 29 verification

- Pure Kotlin compilation and execution passed for all eight rules harnesses, including an all-pairs shortest-path comparison against an independent Floyd–Warshall reference for the 11-location road graph.
- The 2,000-case deterministic realm-day differential suite passed as part of `RulesCheck`.
- Android/Compose compilation, APK assembly, emulator launch and physical-device testing remain unperformed; the pure Kotlin suite does not validate Android UI/runtime integration.
