# Version 85 — Part I stabilization checkpoint

- Consolidated the V81 lifecycle persistence fix, V82 navigation/reset audit, V83 Android resource-tree cleanup and V84 manual-slot isolation rules.
- Revalidated the schema-11 required-key registry against the literal keys written by `MainActivity.save()`.
- Added a final source-level audit script for persistence ordering, checksum policy, rollback path, lifecycle-save gate, slot-key isolation, drawable references and AndroidManifest XML.
- Kept the save schema at 11; no new gameplay field or migration was introduced.
- Part I is ready for the next controlled gate only after a real Android/Compose Gradle build and emulator/device smoke test. V85 is not an APK/release declaration.
