# Version 56 verification notes

Scope: manual save-slot integrity policy.

The current-schema checksum requirement is enforced only after JSON parsing and `SaveManager.validateSnapshot()` have accepted the detached snapshot. This preserves the no-mutation-before-validation invariant. The active preference clear/commit remains later in `SaveSlotStore.loadSlot()`.

Legacy compatibility is explicit: schema 10 and older may omit historical checksums; current schema 11 and future schema markers may not.

Verification performed for V56:
- All pure Kotlin rules modules and all `*RulesCheck.kt` harnesses compiled together with `kotlinc`.
- All 26 test harnesses executed successfully.
- Static checks V30–V56 passed.
- MainActivity delimiter scan, AndroidManifest XML parsing, archive integrity and source packaging are checked during release packaging.

Not performed: Android Gradle/Compose build, APK build, emulator run, or physical-device test. Android SDK/toolchain is not available in this environment.
