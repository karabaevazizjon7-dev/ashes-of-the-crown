# Alpha 23 — Artwork Expansion

## What changed
- Expanded the scene-art mapping for the final archive revelation and the battle section.
- Added character-scene artwork switching for Garrett, Celesta, Mira, Thomas and Reynar without replacing their established portrait assets.
- Added location-aware illustrations for the kingdom map and province/frontier sections: Winter Outpost, Crow Village, Forest Road, Old Chapel, Old Bridge, Weimar Harbor and Walden Fortress.
- Added contextual images for houses, army, battle, journal/quests, economy, council and daily-life pages.
- Updated the asset catalog to mark only assets actually wired into the current UI as assigned.

## Audit
- 146 individual game-art resources are present in the catalog.
- 54 assets are marked as assigned to a UI section or story scene, 5 portrait concepts remain pending comparison with the established character portraits, and 87 assets remain intentionally unassigned because their canonical character/scene fit still needs review.
- All drawable references in MainActivity.kt resolve to existing Android image resources.
- ZIP integrity is checked after packaging.
- Android Gradle compilation and physical-device screenshot checks have not been run.

Gameplay state, save keys, dialogue text, choice logic and soundtrack were not intentionally modified.
