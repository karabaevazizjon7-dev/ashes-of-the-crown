# V124 technical audit

## Save scenarios covered
1. Complete current-schema snapshot accepted.
2. Schema 10 snapshot migrates missing cooperative-scene markers to neutral defaults.
3. Schema 7 snapshot migrates the selected companion's legacy bond into the correct independent bond track, initializes later companion/story/relationship fields, and initializes the daily upkeep marker to the saved world day.
4. Current-schema world-day/upkeep-day mismatch is rejected.
5. SHA-256 digest shape, exact match, case-insensitive match, altered payload, malformed digest, and legacy missing-digest helper behavior are checked.
6. Current-schema checksum enforcement remains in the Android slot adapter; legacy slots may omit a checksum.
7. Lifecycle gate suppresses exactly one stale Activity stop-save after a slot load, then returns to normal save behavior.

## Test results
- Domain test runner: 36 PASS / 0 FAIL.
- V124 static audit: 8 PASS / 0 FAIL.

## Not verified
- SharedPreferences integration behavior on a real Android runtime.
- Gradle Android/Compose compilation.
- Android Lint.
- Physical-device save/load cycle.

These are explicit remaining release checks, not assumed successes.
