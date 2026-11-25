# Version 53 — World location and travel integrity

## Findings and fixes
- Found that a malformed/obsolete persisted `location` string could survive app startup. Because the route planner could not resolve that origin, every subsequent trip failed and the campaign could become travel-locked.
- Added `WorldManager.normalizeLocation`: legacy alias `Порт Эстервала` maps to `Порт Веймар`; unknown or missing locations recover to `Королевский замок`.
- Added an explicit invalid-origin rejection to `WorldManager.planTravel`, so the pure travel API cannot issue a `Ready` plan from a location absent from the canonical map.
- Applied location normalization both when restoring the active game and when validating manually imported save snapshots.
- Corrected illustrated-map travel estimates to display the actual shortest-path duration from the player's current location instead of a fixed destination hint.
- Added regressions for legacy aliases, missing/corrupt locations, invalid travel origins, and manual-save normalization.

## Compatibility
- Save schema remains 11. No new persisted keys or migration step.
- Valid canonical locations and existing legacy port alias remain compatible.
- Corrupt/unknown location values recover to the capital instead of trapping the player outside the known map.

## Verification limits
- Pure Kotlin compilation and rules tests are separate from the Android/Compose build.
- Android SDK/Gradle build and physical-device testing are not claimed.
