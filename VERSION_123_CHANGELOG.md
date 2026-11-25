# V123 — CI domain-test integration

- Added a CI step that installs the pinned Kotlin Compiler 2.0.21 distribution.
- Added execution of `tools/PART3_DOMAIN_TESTS.py` before Android/Compose compilation and lint.
- Documented the distinction between platform-independent domain tests and the full Android build.
- Added `tools/V123_CI_DOMAIN_TEST_AUDIT.py` to validate the workflow integration and APK deferral.
- No gameplay source, assets, dependency declarations, Android SDK levels, or save schema were changed.
- APK packaging remains deferred.
