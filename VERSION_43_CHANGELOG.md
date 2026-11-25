# Version 43 — Game action separation

- Extracted narrative choice resolution from `MainActivity.kt` into `GameActionRules.kt`.
- Added immutable `GameChoiceState` and `GameChoiceResolution` values.
- Centralized paid-choice validation and bounded resource/reputation updates.
- Rejected choices return the unchanged state; Compose remains responsible for UI, journal, inventory rewards, navigation, and persistence.
- No save-schema change: schema remains 11.
- Added `GameActionRulesCheck.kt` and `V43_STATIC_CHECKS.py`.

This is a focused first step in separating game rules from the large Compose screen; it is not a complete MainActivity rewrite.
