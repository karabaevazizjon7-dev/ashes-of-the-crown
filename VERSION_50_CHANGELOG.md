# Version 50 — Combat State Integrity

- Normalized restored combat state before it enters Compose state: an empty enemy name, non-positive HP, or invalid partial encounter now restores as no active battle.
- Bounded restored enemy names and health to avoid malformed/stale local preferences producing inconsistent battle screens.
- Made player attacks against an already-ended encounter a no-op, protecting against delayed or duplicate UI callbacks.
- Made defeat wound-day calculation safe at integer boundaries.
- Added `BattleIntegrityRulesCheck.kt` for valid/invalid restoration, stale attack callbacks, lethal attacks, and day overflow.
- Save schema remains 11; no migration is required.
- Android device/build validation remains pending because Android SDK/Gradle are not available in this environment.
