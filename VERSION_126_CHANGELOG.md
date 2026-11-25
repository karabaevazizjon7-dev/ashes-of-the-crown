# V126 — Long Campaign Stability

- Added deterministic 180-day campaign endurance coverage using the existing world clock and realm-day settlement domain managers.
- Added province, army, guard, provisions, gold and crisis-counter bounds assertions after each daily settlement.
- Added bidirectional route-duration and path-endpoint checks for every declared road.
- Added extreme world-clock arithmetic coverage near `Int.MAX_VALUE`.
- No Android UI, save schema, story content, or APK packaging changes.
