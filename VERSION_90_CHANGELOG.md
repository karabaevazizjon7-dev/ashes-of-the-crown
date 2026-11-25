# Version 90 — atlas, inventory and relationships workspace

## Scope

V90 continues the Part II interface refactor from V86–V89. The map, inventory and relationship pages remain full-width book pages without the secondary illustration column.

## Changes

- Inventory entries are now rendered as compact parchment tiles rather than a long sequence of plain text rows.
- Inventory uses one column on narrow layouts and two columns when available content width reaches 560 dp. Item names wrap and quantities remain visually separated.
- Relationship portraits are grouped into responsive one/two-column rows using the same 560 dp breakpoint.
- Relationship cards use smaller portraits and denser typography to reduce vertical scrolling while preserving role, memory and relationship-meter information.
- Map nodes no longer impose a 155 dp maximum width. Paired map nodes share the available row width, making the atlas use wide screens more effectively.
- Added pure `CollectionLayoutRules` and 202 boundary/layout assertions for breakpoints, row counts and incomplete rows.

## Compatibility

- Save schema remains 11.
- No persistent game-state keys or serialized formats changed.
- Existing route calculations, unlock conditions and travel callbacks are unchanged.
- Existing drawable resources are retained.

## Validation

- All 33 `*RulesCheck.kt` suites compiled together with the pure Kotlin game modules and passed.
- Collection layout test passed 202 assertions.
- Static checks V30–V90 passed after updating the historical V82 navigation check to recognize the current compact navigation rail.
- Drawable references in `MainActivity.kt`: 159 found, 0 missing.
- AndroidManifest XML parse passed.
- MainActivity delimiter scan passed.
- Full Android/Compose build and on-device visual validation were not performed; Android SDK/Gradle toolchain is unavailable in this environment.
