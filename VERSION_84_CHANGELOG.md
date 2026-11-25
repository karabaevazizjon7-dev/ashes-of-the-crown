# Version 84 — manual-slot isolation audit

- Extracted slot-key filtering into pure `SaveSlotRules`, used by both snapshot creation and campaign restoration in `SaveSlotStore`.
- Explicitly codified that campaign snapshots never contain app-wide `music_enabled` or any `slot_*` metadata; applying a slot never overwrites those values.
- Added `SaveSlotRulesCheck.kt` for campaign-key inclusion, audio preference isolation and all three slot metadata prefixes.
- Added static checks for validation-before-clear ordering and rollback after a failed commit.
- No persistence format change: schema remains 11 and the existing JSON/checksum payload contract is unchanged.
