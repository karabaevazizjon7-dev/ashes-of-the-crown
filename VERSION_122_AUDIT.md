# V122 technical audit

Date: 2026-11-25
Base: V121 archive contents (project tree labelled `ashes_alpha27_part3_v120` internally).

## Executed results

| Check | Result |
|---|---:|
| Android project preflight — source/configuration checks | 8 PASS |
| Gradle Wrapper availability | BLOCKED — no `gradlew` or wrapper properties |
| Gradle executable availability | BLOCKED — `gradle` not on PATH |
| Android SDK availability | BLOCKED — no SDK path or SDK environment variable |
| CI pipeline audit | 10/10 PASS |
| V120 release-readiness audit | 14/14 PASS |
| Platform-independent Kotlin domain tests | 35/35 PASS |
| V122 release gate | 4/4 PASS |
| MainActivity drawable references | 159 resolved, none missing |
| Bundled music | 7 non-empty OGG files |
| Save schema | remains version 11 |

The 35-suite Kotlin domain test run completed successfully. It emitted three compiler warnings in `WorldRulesCheck.kt` for unnecessary casts; these were warnings, not failures. The full Android module was not compiled.

## Build status

Full Android/Compose compilation and Android Lint remain unverified because this execution environment has no Gradle Wrapper, Gradle executable, or Android SDK. The repository's GitHub Actions workflow is configured to provision JDK 17, Android SDK 35 and Gradle 8.9, then run `:app:compileDebugKotlin` and `:app:lintDebug`; however, the workflow has not been executed from this session.

No APK was generated. APK packaging remains deliberately deferred to the final release gate.

## Change safety

V122 changes only validation tooling and documentation. No gameplay source, assets, Gradle dependency declarations, SDK levels, or save schema were changed. The previous V121 archive remains untouched.
