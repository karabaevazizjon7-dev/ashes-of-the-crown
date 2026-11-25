# Ashes of the Crown — Version 47

## Save validation hardening

- Extended bounded normalization to legacy companion relationship values (`bond_thomas`, `bond_mira`, `bond_reynar`).
- Limited `social_conflict` to its actual 0–3 progression range.
- Bounded companion memory, random-event, crafting, and encounter counters to 0–1,000,000.
- Added regression coverage for positive and negative out-of-range values.
- Kept save schema at 11: this is validation-only normalization and requires no migration.

## Verification

Pure Kotlin rules tests and static checks are run for this archive. Android/Compose packaging and on-device testing are not claimed unless performed separately.
