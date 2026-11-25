# V86 — UI audit

Scope: layout, responsive typography, scene illustration assignment, navigation simplification, offline soundtrack provenance.

Verified by static checks:
- map/inventory/people/companions/dialogues are marked as full-width workspaces;
- the duplicate right navigation rail is removed;
- right artwork panel contains only a single image, no consequences panel or text collage overlay;
- all 21 story scene indices resolve to a curated single-scene `scene_art_*` illustration set; Grey Ford uses the bridge/river-crossing scene, not the 2x2 mountain-route collage;
- story portrait and text sizes are reduced; title uses the system cursive family;
- compact choice rows retain click handlers and call the existing `makeChoice` action;
- schema documentation says 11 and no persistence keys or schema number were changed;
- all seven existing bundled music assets remain local/offline.

Limitations:
- external audio files could not be downloaded in this runtime because outbound DNS/network access failed. No unlicensed or placeholder tracks were inserted.
- no Android SDK/Gradle toolchain is available here; Android/Compose compilation and device rendering remain pending.
