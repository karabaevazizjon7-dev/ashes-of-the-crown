# Ashes of the Crown — Working Version 26

## Player progression extraction
- Added pure Kotlin `PlayerProgressManager` and immutable progression state/result models.
- Moved XP awards and multi-level progression out of the Compose screen's inline calculation.
- Preserved the existing level threshold rule (`level * 100`) and one skill point per gained level.
- Added safe handling for non-positive rewards, malformed negative loaded values, and integer overflow.

## Journal extraction and bounds
- Added pure Kotlin `JournalManager` for journal normalization and append operations.
- Routed all direct journal append operations in `MainActivity.kt` through one bounded adapter.
- Retained the newest 500 non-blank entries to prevent unbounded journal growth and oversized preference saves.
- Existing journal delimiter and save key are unchanged; no save schema migration is introduced.

## Compatibility and limitations
- Save schema remains 7. Existing progression keys and journal serialization are unchanged.
- This is a focused extraction, not a complete conversion of Compose state into a single GameState.
- Android Gradle build/device testing is not claimed; pure Kotlin and static integration checks are listed in the validation report.

## Validation actually executed
- `RulesCheck.kt`: passed, including 2,000 deterministic differential realm-day cases.
- `QuestRulesCheck.kt`: all assertions passed.
- `EconomyRulesCheck.kt`: transactions, treasury, provisioning, recruitment, training, and GameState snapshots passed.
- `InventoryRulesCheck.kt`: all assertions passed.
- `ProgressJournalRulesCheck.kt`: XP thresholds, multi-level grants, carry-over, invalid values, overflow handling, journal trimming, and blank-entry handling passed.
- Static integration checks: no direct `journal.add(...)` calls remain in MainActivity; XP grants delegate to PlayerProgressManager; save schema remains 7; AndroidManifest.xml parses.
- Not run: Android Gradle build, APK assembly, emulator, or physical-device tests. The environment does not contain the project Gradle wrapper or a configured Android SDK/Gradle installation.
