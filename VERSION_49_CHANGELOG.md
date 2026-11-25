# Ashes of the Crown — Version 49

## Deep quest/inventory integrity pass

- Added atomic `InventoryManager.applyQuestDelta`: quest item consumption and reward delivery now commit together or leave the original inventory untouched.
- Medicine crate quest no longer advances its saved stage or pays rewards if its item transaction cannot be completed (for example, when the bag is full).
- Black Rose chapter progression now requires the sealed letter and final chapel seal to be present before advancing and paying rewards.
- Seven Houses Council finalization now requires the council token to be present before the chapter is marked complete and rewards are paid.
- Existing unique quest evidence is treated as already delivered, preventing a duplicate-item state from permanently blocking a quest.
- Four personal companion-story reward paths now set their completion flags and grant relationship/reputation rewards only after the associated evidence item is present; a full inventory leaves the story retryable.
- Added regression cases for full inventory, atomic replacement at capacity, and idempotent delivery of unique evidence.
- Save schema remains 11; no save migration required.

## Validation

Pure Kotlin tests and static checks are run for this version. Android/Compose packaging and device execution are not claimed unless separately performed.
