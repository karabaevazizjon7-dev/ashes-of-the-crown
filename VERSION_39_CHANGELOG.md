# Ashes of the Crown — Version 39

## Companion team events 1.0

- Added `CompanionTeamEventManager.kt`, a pure Kotlin rules module containing six one-time cooperative scenes, one for each companion pair.
- Each scene has a distinct narrative and two approaches: cooperate closely or make a measured compromise.
- Events unlock only when the pair affinity is at least 10 and each companion's individual bond with the hero is at least 5.
- Cooperation grants +6 pair affinity and +2 individual bond to each participant. A measured compromise grants +2 pair affinity and +2 individual bond to each participant. All values remain bounded by the existing relationship rules.
- The companion page presents the next unfinished team event. Both participants must be physically present and free under the existing NPC timetable; availability is checked again when the player chooses an outcome.
- Each event can be completed once. A campaign-day guard limits team events to one per day, and decisions are added to the journal.
- Completion uses a six-bit mask, avoiding six separate preference fields.

## Save compatibility

- Save schema advanced from 10 to 11.
- Added `companion_team_event_flags`, `companion_team_event_day` and `companion_team_event_report`.
- Schema 1–10 saves retain previous progress and receive neutral defaults for the new team-event state. Existing pair affinity, personal bonds and story stages are not reset.

## Verification

- All 15 pure Kotlin regression entry points compiled and passed, including 2,000 differential realm-day cases.
- Static integration checks V30–V39 passed.
- `MainActivity.kt` lexical delimiter/string/comment check passed; all 113 literal save keys written by the Activity are registered in `SaveManager` or covered by dynamic dialogue-key validation.
- `AndroidManifest.xml` parsed successfully.
- `SaveSlotStore` and related save classes type-checked using temporary Android/JSON stubs. This is not a full Android SDK build.
- No Android/Compose build, APK, emulator run or physical-device test was performed.
