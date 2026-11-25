# Part III — первая полная Android-проверка через GitHub Actions

Проект подготовлен к запуску проверки компиляции и Android Lint в GitHub Actions. Это ещё не подтверждение успешной Android-сборки: workflow необходимо запустить в репозитории и изучить его результат.

## Что делает workflow

Файл `.github/workflows/android.yml`:
1. Проверяет структуру проекта.
2. Устанавливает JDK 17 и Python 3.12.
3. Устанавливает Android SDK Platform 35 и Build Tools 35.0.0.
4. Устанавливает Gradle 8.9 через `gradle/actions/setup-gradle` — Gradle Wrapper в проекте пока не включён.
5. Устанавливает Kotlin Compiler 2.0.21 для платформенно-независимых тестов.
6. Запускает `tools/V127_ANDROID_RELEASE_AUDIT.py`.
7. Компилирует доменные Kotlin-модули и запускает тестовые наборы командой `python tools/PART3_DOMAIN_TESTS.py`.
8. Запускает исторические аудиты V102 и V120–V126.
9. Выполняет `:app:compileDebugKotlin` и `:app:lintDebug`.

Основной workflow намеренно не выполняет `assembleDebug`, `assembleRelease` или `bundleRelease`. Отдельно добавлен `.github/workflows/android-apk-manual.yml`: он запускается только вручную на ветке `main` и только если в поле подтверждения введено ровно `YES`. Перед `:app:assembleDebug` он повторно выполняет компиляцию Kotlin и Android Lint, затем публикует debug APK как GitHub Actions artifact на 14 дней. Это отладочный APK, не подписанный релизный пакет для Google Play.

## Как запустить

1. Создайте GitHub-репозиторий.
2. Загрузите **содержимое папки `project`** в корень репозитория, чтобы `settings.gradle.kts`, `app/` и `.github/` находились непосредственно в корне.
3. Отправьте изменения в ветку `main` или `master`.
4. Откройте вкладку **Actions** → **Android compile verification** и запустите проверку.
5. При необходимости выберите **Run workflow**.
6. Дождитесь завершения job `compile-check`.

## Что прислать, если проверка упала

Скачайте полный лог job или скопируйте:
- первую ошибку Gradle, начиная с `* What went wrong:`;
- строки Kotlin/Compose с `e:` и контекстом;
- отчёт `android-lint-report`, если lint завершился.

Не присылайте только последнюю строку `BUILD FAILED`: нужен первый конкретный сбой.

## Критерии готовности к следующему этапу

- `:app:compileDebugKotlin` завершился успешно;
- `:app:lintDebug` завершился успешно или все замечания классифицированы и решены;
- затем выполнен smoke test на эмуляторе/телефоне: запуск, ключевые экраны, сохранение/загрузка, сворачивание и восстановление приложения.

Только после этих проверок можно переходить к отдельному разрешённому этапу создания и подписания APK. До этого APK не создавать.

## Ручная сборка тестового APK (не запускать до прохождения compile/lint и разрешения)

После успешного compile/lint и отдельного решения о сборке откройте **Actions** → **Manual Android debug APK** → **Run workflow**. Запуск возможен только из ветки `main`; в поле `confirm_build` нужно ввести `YES`. Workflow ещё раз выполняет compile/lint, затем создаёт `app-debug.apk` и сохраняет его в артефактах Actions на 14 дней. Это debug APK для установки и тестирования, а не подписанный production/release APK.
