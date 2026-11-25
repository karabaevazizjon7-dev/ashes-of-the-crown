# Ashes of the Crown — Version 42

## Cross-system royal council priorities

- Added `RealmPriorityManager`, a pure Kotlin, deterministic and read-only evaluator combining treasury, army supply, guard confidence, province loyalty/crisis stages, and faction reputation.
- Added ranked council recommendations for low provisions, depleted treasury, unstable provinces, weak guard confidence, legitimacy gaps, trade pressure and competing centers of influence.
- Integrated a live "Приоритеты королевского совета" card into the Esterval overview. It recomputes from current campaign state and does not create new save keys or alter player resources.
- Save schema remains 11; no migration required.

## Verification

- Pure Kotlin regression suite added for calm, emergency, limited-input and ranking cases.
- Static integration checks V42 added.
- Full Android/Compose build and device testing were not performed.
