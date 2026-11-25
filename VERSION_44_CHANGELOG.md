# Ashes of the Crown — Version 44

## World clock extraction

- Added pure Kotlin `WorldClockManager` and immutable `WorldClockAdvance` result.
- Centralized hour normalization, day rollover, elapsed-hour handling, upkeep-day planning, time-of-day phase selection, and clock transition messages.
- `MainActivity.advanceWorldTime` now delegates clock arithmetic and time messaging to the manager while retaining the established ordered daily settlement pipeline (army provisions, province economy, crisis consequences, world events, reports, journal, save).
- Used `Long` for intermediate hour arithmetic and bounded the resulting day to prevent integer overflow on malformed/extreme inputs.
- Kept save schema at 11; no new persisted state was introduced.

## Verification

- Added `WorldClockRulesCheck` for zero/negative/normal/multi-day advances, overdue upkeep, sunrise/dusk boundaries, invalid clock normalization, and overflow boundaries.
- Added `V44_STATIC_CHECKS.py` for integration and architecture regression checks.
- Full Android/Compose build and device testing are not claimed unless run separately.
