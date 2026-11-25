# Version 54 — legacy preference restore hardening

- Found a consistency gap: imported/manual saves were normalized through `SaveManager`, but direct `SharedPreferences` restoration in `MainActivity` still read several numeric values without applying the same bounds.
- Added `SaveManager.normalizeInteger`, reusing the exact bounded-field registry used by manual-save validation.
- Applied the shared normalizer to directly restored gold, health, reputation, guard, influence, faction standings, quest path/stage, companion memories, event counters, quest/social flags, crafting/expedition/battle counters, experience, level, skill points and skills, and encounter count.
- Left opaque `weather_seed` untouched; it is not a bounded gameplay resource.
- Added extreme-value regression coverage, including `Int.MIN_VALUE` and `Int.MAX_VALUE` cases.
- Save schema remains 11; no migration or reset is introduced.
