# Version 83 — Android resource-tree cleanup

- Found 146 image files in `drawable-nodpi/game_art/` that were byte-identical to images already present directly in `drawable-nodpi/`. This nested folder was a staging/archive copy, not a distinct runtime asset set.
- Removed only those duplicate copies from the Android resource tree. Kept every drawable used by the game unchanged.
- Moved `README_ASSETS.txt` and `ASSET_CATALOG.json` to `docs/visual_assets/alpha4/`; the full 146-entry provenance catalog is retained.
- Added a resource integrity check: all 146 catalog images exist, all 150 Kotlin `R.drawable` references resolve, and `drawable-nodpi` contains no nested staging directory.
- No visual asset was edited, recompressed or replaced. Save schema remains 11.
