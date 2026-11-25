# Version 51 — Progression and Choice Arithmetic Integrity

- Fixed a critical integer edge case in narrative choice costs: negating `Int.MIN_VALUE` as an `Int` could wrap and incorrectly turn an unaffordable choice into an accepted one. Costs are now evaluated as `Long`.
- Reworked all narrative-choice numeric deltas to add through `Long` and clamp before converting back to `Int`, preventing overflow from reversing the intended effect.
- Capped live choice gold at the same 1,000,000 maximum enforced by save validation.
- Aligned live player progression normalization with persisted limits: level 1–1,000, experience 0–1,000,000, skill points 0–100,000.
- Added a hard level cap and skill-point cap so live state cannot drift outside values accepted by SaveManager.
- Expanded GameActionRules boundary coverage and added ProgressionIntegrityRulesCheck.kt.
- Save schema remains 11; no migration is required.
- Android/Compose build and physical-device testing remain pending; the available environment does not provide the Android SDK/Gradle toolchain.
