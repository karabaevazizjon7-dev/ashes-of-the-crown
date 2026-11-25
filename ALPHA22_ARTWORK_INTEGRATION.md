# Alpha 22 — Artwork integration

- Replaced generic repeated section illustrations with category-specific game art.
- Story scenes 0–17 now select distinct, context-matched existing artwork (letter/archive, armory, garden, council, city panorama, travel, market, throne room, hideout, tavern, secret meeting, rescue, betrayal, etc.).
- Dedicated section artwork is now mapped for world map, hero, people/companions/dialogues, houses, items, army, battle, journal/quests, provinces, economy, council, frontier/expeditions, life and about.
- Updated `ASSET_CATALOG.json` status for 27 assets actually referenced by the UI. The remaining assets are retained and intentionally marked unassigned pending canon scene/character mapping; no invented claim that every image is finished.
- Gameplay state, save keys, dialogue/choice logic and music were not intentionally changed.

## Verification
- Resource-name audit: all newly referenced game-art resource names exist in `drawable-nodpi/game_art`.
- Archive integrity: checked after packaging.
- Android Gradle build and device screenshot validation: not performed in this environment.
