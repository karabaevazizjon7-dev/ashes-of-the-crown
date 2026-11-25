# Version 55 — complete current-schema save validation

- Deep-audited the manual save-slot lifecycle: `SaveSlotStore.loadSlot()` validates a detached snapshot and then clears active preferences before applying it.
- Found a data-loss edge case: schema-11 snapshots were accepted when only the six legacy baseline keys existed. Missing fields then silently fell back to fresh defaults after the preference clear, discarding campaign progress.
- Added `SaveManager.currentSchemaRequiredKeys`, a fixed-key registry matching every non-dynamic key written by `MainActivity.save()`.
- `validateSnapshot()` now rejects schema-11 snapshots missing any fixed current-schema field before they can be loaded into active preferences.
- Kept per-NPC dialogue keys optional because only the currently selected NPC's dynamic key is written by the existing save path.
- Preserved schema-10 and older migration behavior; save schema remains 11.
- Added `SaveSnapshotCompletenessRulesCheck.kt`, covering a complete schema-11 snapshot, removal of each required field, and legacy schema-10 acceptance.
