# Version 81 — lifecycle persistence and empty-outcome save integrity

- Added a lifecycle safety save on `ON_STOP`, so the current Compose campaign state is flushed to `SharedPreferences` when the Activity leaves the foreground.
- Added `SaveLifecycleGate`: after a manual slot is loaded, the outgoing Activity suppresses exactly its next stop-save. This prevents stale in-memory state from overwriting the newly applied slot immediately before `Activity.recreate()`.
- Fixed a current-schema manual-slot edge case: `SharedPreferences.Editor.putString(key, null)` removes the key. A fresh campaign has a null `lastOutcome`, while schema 11 requires the `outcome` key; previously this could make a valid fresh campaign fail manual-slot validation. The save now serializes null as an empty string, and restoration maps blank text back to null so the UI behavior is unchanged.
- Added `SaveLifecycleGateRulesCheck.kt` for normal stop, one-shot suppression and resumed persistence. Extracted nullable text encode/decode into `SaveDataRules` and added `OptionalSaveTextRulesCheck.kt` so the null/blank behavior is executable rather than only source-checked.
- Save schema remains 11. No gameplay state or manual-slot format was changed.
