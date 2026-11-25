# Ashes of the Crown — Version 41

## Crisis consequences: faction reactions and regional spillover

- Added `CrisisConsequenceManager`, a pure Kotlin rules module that compares a province's previous and current crisis stages.
- Political consequences now trigger only when a province crosses into a new stage; remaining in the same stage does not repeatedly apply the same penalty.
- Crown reputation reacts to unrest, escalation and revolt. The people and Black Rose tracks react to escalation/revolt, and a revolt in Port Holdings also damages merchant reputation.
- Escalation and revolt reduce loyalty in adjacent provinces according to an explicit four-province adjacency map.
- Each transition is included in the daily realm report and campaign journal.
- No new save fields were introduced; existing crisis counters and faction reputation values remain the source of truth. Save schema stays at 11.

## Verification

- 18 pure Kotlin test mains compiled and passed, including the new crisis-consequence regression suite.
- Existing differential realm-day suite passed 2,000 cases.
- Static integration scripts V30–V41 passed.
- AndroidManifest.xml parsed successfully.
- Full Android/Compose build and device testing were not performed.
