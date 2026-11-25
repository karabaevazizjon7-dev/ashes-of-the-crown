# V127 — audit results

## Confirmed in the available workspace
- `tools/PART3_DOMAIN_TESTS.py`: **38/38 test suites PASS**. Compilation used 36 platform-independent Kotlin source files and 38 test files. Four harmless Kotlin warnings about unnecessary casts were emitted.
- `tools/V102_CI_PIPELINE_AUDIT.py`: **10/10 PASS**.
- `tools/V120_RELEASE_READINESS_AUDIT.py`: **14/14 PASS**.
- `tools/V123_CI_DOMAIN_TEST_AUDIT.py`: **8/8 PASS**.
- `tools/V124_SAVE_RECOVERY_AUDIT.py`: **8/8 PASS**.
- `tools/V125_CAMPAIGN_LOOP_AUDIT.py`: **8/8 PASS**.
- `tools/V126_LONG_CAMPAIGN_AUDIT.py`: **7/7 PASS**.
- ZIP input archive integrity: passed before modification.

## V127 audit scope
The new `tools/V127_ANDROID_RELEASE_AUDIT.py` validates Android project configuration, XML parsing, manifest launcher declaration, explicit resource references, image file integrity, OGG signatures, save schema 11, CI toolchain pins and deferred APK policy.

## Not yet confirmed
- Full Android Gradle/Compose compilation (`:app:compileDebugKotlin`).
- Android Lint (`:app:lintDebug`).
- GitHub Actions execution (workflow has been prepared but not run against the user's repository).
- Emulator/physical-device smoke tests, audio playback, lifecycle and on-device save/load.
- APK packaging/signing. APK intentionally not created.

The project is prepared for a GitHub Actions compile/lint run, but must not be described as build-verified until that workflow succeeds.
- Added a separate manual debug-APK workflow guarded by `main` + explicit `YES`; it repeats compile/lint before packaging and uploads only a debug artifact. This workflow was statically audited but not executed.
