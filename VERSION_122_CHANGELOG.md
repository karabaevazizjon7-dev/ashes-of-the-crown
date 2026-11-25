# V122 — reproducible Part III validation gate

## Scope
V122 adds a repeatable release-gate runner for source-level and configuration checks. It does not modify gameplay code, Android dependencies, SDK targets, save data, or release packaging policy.

## Added
- `tools/V122_RELEASE_GATE.py`: runs project preflight, CI configuration audit, V120 release-readiness audit, and verifies APK packaging remains deferred.
- `VERSION_122_AUDIT.md`: records the checks actually executed and the outstanding Android build blockers.

## Verified in this environment
- Project preflight: 8 source/configuration checks passed; 3 environment prerequisites are blocked (Gradle Wrapper, Gradle executable, Android SDK).
- CI pipeline audit: 10/10 passed.
- V120 release-readiness audit: 14/14 passed.
- Platform-independent Kotlin domain suite: 35/35 suites passed when run separately using `tools/PART3_DOMAIN_TESTS.py`.
- V122 release gate: 4/4 checks passed.

## Not verified
- Full Android/Compose compilation.
- Android Lint execution.
- Emulator or physical-device runtime testing.
- APK packaging (intentionally deferred until final release approval).
