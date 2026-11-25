# V81 audit record

## Findings

1. `lastOutcome` is nullable, but the schema-11 completeness registry requires the `outcome` key. `putString("outcome", null)` removes the key in Android SharedPreferences. This made a fresh campaign snapshot incomplete for manual-slot validation. Fixed by serializing null as `""` and mapping blank values back to null on restore.
2. Activity state was written on explicit gameplay actions but had no lifecycle safety save. Added `ON_STOP` persistence.
3. Loading a manual slot commits the new snapshot and recreates the Activity. A generic `ON_STOP` save would otherwise risk writing the outgoing Activity's stale Compose state over the just-loaded snapshot. Added a one-shot suppression gate for that exact transition.

## Verification scope

- Pure Kotlin gate and optional-text encode/decode tests added.
- Static integration checks cover null-outcome serialization/restoration, lifecycle callback, one-shot suppression, and schema registry alignment.
- Full Android lifecycle runtime behavior still requires emulator/device verification; it is not claimed by this source-level pass.
