# Working version 57 — reject overflowing save-schema identifiers

- Deep-audited the schema-version parser in `SaveManager.validateSnapshot()`.
- Found a real integer-narrowing defect: an untrusted schema value such as `4_294_967_307L` wraps to `11` when converted directly with `toInt()`, allowing a malformed version marker to masquerade as the current schema.
- Validate the schema as a `Long` before narrowing: values below 1 or above `Int.MAX_VALUE` are rejected, and versions newer than the supported schema are rejected explicitly.
- Added `SaveSchemaBoundaryRulesCheck.kt` for current, overflow-wrapped, out-of-range, fractional and negative schema markers.
- No save keys, gameplay values, migration rules or schema number changed. Current schema remains 11.
