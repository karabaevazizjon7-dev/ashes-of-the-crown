# Alpha 26 — UI extraction

- Extracted the province-management page from `AshesGame` into `ProvincePageContent`.
- Kept province tax collection, investment, steward appointment, daily income, journal entries, and save calls in the original state-owning scope and passed them as callbacks.
- The component receives the existing province state lists and display values; save keys and state initialization are unchanged.
- Bumped Android version to `6.0.0-alpha26` (versionCode 82).

Validation performed: source-level checks and archive integrity checks. A full Android/Kotlin build has not been run in this environment.
- Extracted the treasury/economy page into `EconomyPageContent`; tax collection, treasury upgrades, provisions purchases, journal entries, and save calls remain in the state-owning `AshesGame` callbacks.
- Extracted the army page into `ArmyPageContent`; recruitment and guard training retain their original costs, state changes, time advancement, journal entries, and save calls.
- Structural delimiter scan passed after these extractions. Full Kotlin/Android compilation remains unverified because no Gradle executable or Gradle wrapper is present in this runtime.
