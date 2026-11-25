# Ashes of the Crown — Version 38

## Companion relationships 1.0

Version 38 adds a separate relationship layer between companions. These affinities are independent of the player's personal bond with each companion and independent of each companion's personal-story chapter.

### Six persistent pair relationships

- Garrett Morn ↔ Celeste
- Garrett Morn ↔ Mira
- Garrett Morn ↔ Thomas
- Celeste ↔ Mira
- Celeste ↔ Thomas
- Mira ↔ Thomas

Each pair has a dedicated disagreement scene. The active scene rotates by campaign day, so the six pairings recur in a predictable cycle.

### Player choices

- Mediate: improves the pair's shared affinity by 4.
- Support one companion: strains the pair's shared affinity by 3, with a report identifying whom the player backed.

Affinity is clamped to -100..100. Each decision is recorded in the journal and only one team-conflict resolution can be made per campaign day. Both companions must be present at the player's current location and available according to the NPC timetable; the click handler rechecks availability.

### Save compatibility

Save schema advances from 9 to 10. Six new affinity values, the daily resolution marker, and the latest report are registered. Schema 9 and earlier saves receive neutral defaults for missing pair affinities; existing hero-to-companion bonds and personal-story stages are preserved.

### Verification

- 14 pure Kotlin rule-test mains pass, including the new companion-relationship test.
- 2,000 differential realm-day checks pass.
- Save migration checks cover schema 7 and schema 9 → 10.
- Static integration checks V30–V38 pass.
- SaveSlotStore type-checks against temporary Android/JSON stubs (not a real Android SDK build).
- MainActivity delimiter scan and literal save-key registry check pass.
- AndroidManifest.xml parses and ZIP integrity is checked.

A full Android/Compose build and on-device test have not been performed.
