# Working version 56 — require integrity hashes for current-schema manual saves

- Audited the manual-slot checksum path. `SaveManager.checksumMatches()` intentionally accepts a missing hash for historical legacy slots, but `SaveSlotStore.loadSlot()` applied that permissive rule to current schema 11 as well.
- A schema-11 slot with its checksum metadata removed could therefore bypass the corruption-detection layer while still passing structural validation.
- Changed the load sequence to parse and validate the detached snapshot first, then apply a schema-aware checksum policy before any active preferences are cleared or replaced.
- Current schema 11 (and any future schema) now requires a non-empty SHA-256 checksum. A present checksum must still match the exact payload.
- Legacy schema 10 and older slots may still omit hashes for backward compatibility; if a legacy slot has a hash, it must match.
- Added regression coverage for current, future and legacy schema checksum requirements.
- Save schema remains 11; no gameplay state, save keys, or migration format changed.
