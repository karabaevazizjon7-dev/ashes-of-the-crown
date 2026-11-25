# V127 — Android Release Readiness Gate

- Reworked GitHub Actions ordering so JDK 17, Android SDK 35 and Gradle 8.9 are installed before environment preflight.
- Increased workflow timeout and added bounded step timeouts for domain tests and Android compile/lint.
- Added `tools/V127_ANDROID_RELEASE_AUDIT.py` to validate Android XML, explicit Kotlin/XML resource references, image integrity, OGG signatures, SDK/plugin pins, save schema and the no-APK-before-release policy.
- Added `.gitignore` for local Gradle/Android Studio/build artifacts.
- Updated the Russian build handoff guide.
- No gameplay logic, save data schema, or APK packaging was changed.
- Added a separate manually triggered debug-APK workflow. It only runs on `main` with the exact `YES` confirmation, repeats compile/lint before `assembleDebug`, and uploads the debug artifact for 14 days. The normal push/verification workflow still never packages APK.
