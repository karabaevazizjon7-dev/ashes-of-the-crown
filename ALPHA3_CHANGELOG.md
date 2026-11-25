# Alpha 3 — soundtrack routing groundwork

- Preserved the existing save keys, story scenes, location art and original ambient audio.
- Replaced the single fixed music player setup with location/event-based soundtrack slot selection.
- Added optional resource slots: `music_court`, `music_city`, `music_nature`, `music_harbor`, `music_mystery`, `music_battle`.
- Missing slots fall back to the original `estervale_ambient.ogg`; the game remains playable without new tracks.
- Added `docs/SOUNDTRACK_PLAN.md` with licensing rules and candidate CC0/CC-BY sources.
- Added `docs/ART_DIRECTION.md` describing the first 30–50 scene illustration set.

Limitations: no additional music files or new scene illustrations are bundled in this alpha. Gradle compilation and APK build have not been run because Gradle is not installed in the current environment.
