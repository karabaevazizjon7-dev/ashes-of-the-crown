# Ashes of the Crown — Version 35

## Companion relationships 2.0 and save schema 8

### Added
- Four independently persisted companion bonds: Garrett, Celeste, Mira and Thomas.
- A character-specific personal moment for each companion.
- Three explicit conversation approaches: support, ask about the past, and humor.
- Character-specific reactions and bounded bond changes (-100..100).
- A once-per-campaign-day conversation guard, with the result added to the journal.
- Personal conversations require the selected companion to be physically present and free according to the existing NPC timetable; availability is rechecked when the choice is tapped.
- Schema 7 → 8 migration for manual save slots. The legacy single `companion_bond` value is assigned to the companion selected in that save; other missing companion bonds start at neutral 0. Existing values already present for the new keys are preserved.
- New-game reset for all four bonds and the daily conversation marker.

### Corrected
- A frontier story reward that previously modified whichever companion happened to be selected now correctly improves Mira's companion bond.
- Switching the selected companion no longer reuses another companion's bond value.

### Compatibility and verification
- `SaveManager.CURRENT_SCHEMA` is now 8.
- Existing schema 1–7 snapshots remain accepted; schema 7 receives the explicit companion-bond migration.
- The old `companion_bond` key is still written as a compatibility mirror for the currently selected companion.
- All 13 pure Kotlin test entry points compiled together and passed.
- Static integration checks V30–V35 passed; AndroidManifest.xml parsed successfully; ZIP integrity checked.
- No full Android/Compose build, emulator run, or device test was performed in this environment.
