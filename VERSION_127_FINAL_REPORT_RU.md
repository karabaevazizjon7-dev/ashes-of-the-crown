# Пепел короны — V127: подготовка к GitHub Android CI

## Что изменено
- Переставлены этапы workflow: Android SDK и Gradle теперь устанавливаются до preflight-аудита, чтобы CI проверял уже подготовленную среду.
- Увеличено общее время workflow до 60 минут; для доменных тестов и Android compile/lint заданы отдельные лимиты.
- Добавлен глубокий статический аудит Android XML, manifest launcher, явных `R.*`/XML resource references, изображений, OGG signatures, SDK/toolchain, save schema и запрета преждевременного APK.
- Добавлен `.gitignore` для Gradle, Android Studio и локальных build outputs.
- Переписана инструкция по загрузке проекта в GitHub.

## Фактические результаты, полученные при проверке V126
- Domain Kotlin suite: **38/38 PASS**.
- V102 CI audit: **10/10 PASS**.
- V120 release audit: **14/14 PASS**.
- V123 CI/domain audit: **8/8 PASS**.
- V124 save recovery audit: **8/8 PASS**.
- V125 campaign integration audit: **8/8 PASS**.
- V126 long campaign audit: **7/7 PASS**.
- При компиляции доменных тестов: четыре предупреждения о ненужных cast, ошибок нет.

## Граница подтверждения
Это глубокая проверка исходников, ресурсов, игровых доменных модулей и конфигурации CI. Полная Android/Compose-компиляция и Android Lint не запускались в данной среде. GitHub Actions подготовлен, но ещё не выполнен в пользовательском репозитории. Эмулятор/телефон не тестировались. APK не создавался.

**Статус:** проект подготовлен к запуску GitHub Actions compile/lint; окончательная готовность к APK будет подтверждена только после успешного CI и smoke test на Android-устройстве.
- Добавлен отдельный ручной workflow для debug APK: только ветка `main` + явное подтверждение `YES`; перед упаковкой повторно выполняются compile и lint. Workflow не запускался, APK не создавался.
