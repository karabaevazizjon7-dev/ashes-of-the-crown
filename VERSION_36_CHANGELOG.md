# Ashes of the Crown — Version 36

## Political agenda 1.0 — faction pressure and competing interests

### Added
- Added `FactionAgendaManager.kt`, a derived political layer that interprets the four existing reputation tracks without creating parallel or duplicated state.
- The agenda can surface royal demands for order, merchant pressure for secure trade routes, public demand for relief, the Black Rose's preference for secrecy, direct tension between the Crown and the Black Rose, and the gap between merchant prosperity and public trust.
- Every agenda item contains its source, the current pressure, a possible opportunity/cost framing, and an urgency level. Items are ordered by urgency.
- The Life of Estervale page now displays the three most urgent political issues, with a short explanation of what each faction expects and what trade-off may follow.
- Added regression coverage for balanced relations, high-trust Crown/Rose conflict, low public trust, merchant/public disparity, ordering, and extreme integer inputs.

### Compatibility
- The agenda is computed from existing reputation values. No new save keys, preferences, or schema migration were introduced; schema remains 8.
- Existing saves and the four independent companion bonds from version 35 remain unchanged.

### Verification
- All 13 pure Kotlin test entry points compiled and passed, including the 2,000-case differential day-processing check.
- Static integration checks V30–V36 passed; AndroidManifest.xml parsed successfully.
- The project ZIP integrity is checked after packaging.
- No full Android/Compose build, APK, emulator run, or physical-device test was performed.
