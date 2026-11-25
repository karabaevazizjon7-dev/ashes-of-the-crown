# V84 audit record

Manual slot storage was inspected for cross-campaign leakage. The snapshot and restore paths previously expressed their key exclusions separately. The policy is now centralized in `SaveSlotRules` and used by both paths. The three slot records and music preference remain outside campaign snapshots; campaign fields remain included. Existing validation-before-clear and commit rollback order is statically guarded.

The Android `SharedPreferences` adapter is not claimed as runtime-tested on device. The pure key-policy helper is covered by an executable Kotlin test.
