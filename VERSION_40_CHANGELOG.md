# Ashes of the Crown — Version 40

## Part I: Provincial crisis stages

- Added `WorldCrisisManager`, a pure rules module that derives four readable stages from the existing provincial crisis-day counter: stable, unrest, escalation and revolt.
- Added urgency ranking across provinces, stage descriptions and recommended recovery actions.
- Integrated the crisis summary into daily realm reports.
- Updated province cards to show stage-specific status and guidance.
- Kept the existing crisis resolution choices (12 gold for aid or 4 influence for negotiations).
- No save schema change: the system derives its state from existing `a15_crisis_days`, loyalty and prosperity values. Existing saves remain compatible.

## Verification

- Pure Kotlin rule tests include all crisis stages, urgency ranking, mismatched input lengths and integer-boundary normalization.
- Static integration checks and AndroidManifest XML parsing.
- Full Android/Compose build and device testing are not claimed.
