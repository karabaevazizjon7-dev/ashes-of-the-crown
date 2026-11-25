# Version 52 — Treasury collection integrity

## Changes
- Prevented tax collection when the current world day is invalid or the saved tax marker is on a future day. This closes a replay window if the world clock is rolled back or state is corrupted.
- Clamped merchant reputation to the campaign's supported -100..100 range before calculating tax income.
- Made the final gold addition overflow-safe with `Long` arithmetic and retained the 1,000,000 gold cap.
- Updated the economy screen so its tax button follows the same strict eligibility rule as the transaction manager.
- Added regression cases for future markers, invalid day zero, extreme reputation values, and the gold cap.

## Compatibility
- Save schema remains 11; no migration or new persistent fields.
- No intentional economy balance changes for valid campaign values.

## Verification limits
- Pure Kotlin rule tests and static integration checks are run separately from the Android UI.
- Full Android/Compose build and physical-device testing are not claimed unless performed in an Android SDK environment.
