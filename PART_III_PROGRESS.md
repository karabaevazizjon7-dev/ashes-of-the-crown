# ASHES OF THE CROWN — PART III PROGRESS

## V101–V102 — build readiness baseline
Проверены конфигурация Android и CI. CI предназначен для компиляции Kotlin и Android Lint без ранней упаковки APK.

## V103 — automated domain regression
Добавлен `tools/PART3_DOMAIN_TESTS.py`, который компилирует платформенно-независимые игровые модули и запускает все 35 Kotlin-проверок из каталога tools.

## V104–V119 — системные контрольные точки
Отдельными отчётами зафиксированы результаты проверок боевой системы, инвентаря, экономики, сохранений, прогрессии, квестов, календаря, фракций, компаньонов, NPC, путешествий, сюжета, адаптивных раскладок, ресурсов и CI. Это контрольные точки аудита, а не заявление о 17 независимых изменениях игрового кода.

## V120 — предрелизный контроль
- Kotlin domain compilation + regression: **35/35 suites PASS**.
- CI guardrails: **10/10 PASS**.
- Release readiness audit: **14/14 PASS**.
- Build preflight: **8/11 PASS**; blockers: Gradle Wrapper отсутствует, Gradle не установлен, Android SDK не настроен в текущей среде.

### Ограничения
Полная Android/Compose-сборка, Android Lint, запуск на эмуляторе/телефоне и прослушивание аудио не выполнялись. GitHub Actions workflow подготовлен, но не запускался из этой среды. APK не создавался в соответствии с планом.

### Следующий шаг
Выполнить CI workflow в GitHub или Gradle build в Android Studio/на машине с Android SDK 35. Исправлять только реальные диагностированные ошибки. После успешной компиляции — device smoke test, затем профилирование и финальная упаковка.

## V121 — build handoff и диагностика CI
- Gradle workflow теперь выводит stacktrace и все предупреждения Gradle.
- Android Lint report сохраняется как workflow artifact на 14 дней.
- Добавлено `PART3_ANDROID_BUILD_GUIDE_RU.md` с точными шагами запуска и перечнем диагностических данных.
- `tools/V121_CI_HANDOFF_AUDIT.py` проверяет 10 условий workflow и handoff.
- Полная Android-сборка всё ещё ожидает запуска на машине с Android SDK или в GitHub Actions. В этой среде Gradle/SDK отсутствуют; APK не создавался.
