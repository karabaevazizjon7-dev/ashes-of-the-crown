# V121 — Android build handoff

- Усилен Gradle invocation: `--stacktrace` и `--warning-mode all` для диагностируемых логов.
- Добавлено сохранение Android Lint report как GitHub Actions artifact (14 дней).
- Добавлено русскоязычное руководство запуска первой Android/Compose-проверки через GitHub Actions или Android Studio.
- APK packaging не добавлялся; версия приложения и схема сохранений не менялись.
