# V102 — CI compile gate and APK release control

- Replaced the early APK-producing GitHub Actions workflow with a compile-and-lint verification workflow.
- CI now provisions JDK 17, Android SDK 35, and Gradle 8.9, then runs `:app:compileDebugKotlin` and `:app:lintDebug`.
- Removed automatic APK artifact packaging/upload from push and manual CI runs. APK creation remains reserved for the final Part III release gate.
- Added `tools/V102_CI_PIPELINE_AUDIT.py` to guard the workflow against accidental early APK packaging and configuration drift.
- No gameplay code, save format, app version metadata, or dependency versions changed.
