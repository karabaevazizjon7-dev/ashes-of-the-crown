# Ashes of the Crown — Part II progress

## V86 — initial interface cleanup

This is the first implementation pass based on the user's landscape-screen reference.

### Design decisions now applied
- Keep the story as the primary reading surface.
- Reduce the top HUD and side navigation so they stop competing with the narrative.
- Keep only core navigation visible; move secondary kingdom-management pages under «Ещё».
- Give map, inventory and relationship/people workspaces the full available book width.
- Reserve the story's right page for one scene illustration only.
- Use one coherent scene illustration per story beat, not multi-panel contact-sheet artwork.
- Reduce the protagonist portrait and story typography; use the system cursive face for scene titles.
- Keep the game fully offline; soundtrack files remain bundled locally.

### V87 — story layout density and full-frame artwork
- Further reduced Adrian's portrait, scene heading, story body and choice typography using compact-screen and long-scene sizing rules.
- Removed the separate consequences block from the story reading page to return vertical space to the narrative and choices; outcomes remain in campaign state/journal.
- Changed the right-side artwork from crop to fit so the full illustration remains visible inside the frame.
- Kept the map, inventory, people and companion workspaces full-width.
- No save schema, story state, game-rule or soundtrack changes.
- 31 pure Kotlin regression test mains compiled and passed; V85, updated V86 and V87 static checks passed. Android/Compose build and physical-device visual verification remain outstanding.

## Next Part II work
1. Review all remaining scene and location illustrations for accidental contact sheets and mismatched captions.
2. Expand the narrative branches and consequences without forcing long scrolling on the landscape layout.
3. Integrate additional licensed music as local assets after the audio binaries can be obtained and their license files verified.
4. Build new northern-region content on top of the existing travel, quest, faction and save systems.

## V89 — curated single-frame story artwork
- Replaced the 21-scene story artwork resolver with an explicit one-to-one mapping to `story_v89_00` … `story_v89_20`.
- Added 21 separate 960×540 JPEG resources in 16:9 format. Multi-panel concept sheets were cropped to a single selected frame before inclusion; existing source artwork remains untouched.
- The story art panel now uses `ContentScale.Crop` against preformatted 16:9 assets so the illustration fills its allotted window without a collage or empty fit-bars.
- Added `docs/STORY_ARTWORK_V89.md` documenting the scene-to-artwork mapping.
- V89 static audit: 90 assertions passed, including resource existence, dimensions, image type, unique file hashes, mapping coverage and Manifest XML.
- `StoryLayoutRules.kt` + its 180-case test compiled and passed. Static checks V85–V89 passed after updating earlier UI checks to reflect the V89 intentional switch from Fit to Crop.
- A full compile of all pure modules/tests exceeded the available command time limit; no full Kotlin suite result is claimed for V89. Android/Compose build and device testing remain outstanding.


## V90 — Atlas, inventory and relationships layout

- Inventory converted to compact parchment tiles with adaptive one/two-column layout.
- Relationship portraits converted to responsive cards with smaller portraits and preserved relationship meters.
- Atlas map nodes now share row width instead of being constrained to a fixed 155 dp maximum.
- Added `CollectionLayoutRules` and 202 boundary/layout assertions.
- 33 pure Kotlin regression suites passed; static checks V30–V90 passed; 159 drawable references resolved.
- Save schema remains 11. Full Android/Compose build and device visual validation remain outstanding.
