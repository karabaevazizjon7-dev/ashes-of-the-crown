# Ashes of the Crown — soundtrack integration

The app currently ships with one verified in-project audio asset: `res/raw/estervale_ambient.ogg`.

`MainActivity.kt` now selects a soundtrack slot from the current location and world event. Optional files are discovered by Android resource name; if a slot is not yet populated, playback safely falls back to the original ambient track. Add OGG files to `app/src/main/res/raw/` using these exact lowercase names:

| Resource name | Intended scene |
|---|---|
| `music_court.ogg` | Royal castle, court, council, menu |
| `music_city.ogg` | Market, lower city, village |
| `music_nature.ogg` | Garden, forest road, countryside |
| `music_harbor.ogg` | Weimar harbour and riverfront |
| `music_mystery.ogg` | Investigation, old bridge, chapel, secret events |
| `music_battle.ogg` | Combat, attacks and battles |

Do not add a track until its license and redistribution rights have been checked. CC0 tracks are preferred for a game distributed with audio bundled offline. For CC-BY tracks, retain the author, title, source URL and license text in the in-game credits and this project's credits file. A web page describing a collection is not itself proof that every contained track has the same license; verify each selected file individually.

## Candidate sources (not yet bundled)

- RandomMind, CC0 medieval collection: https://opengameart.org/content/cc0-audio-uploader-randommind
- RandomMind, “Medieval: Exploration”, CC0: https://opengameart.org/content/medieval-exploration
- RandomMind, “Medieval: The Bard's Tale”, CC0: https://opengameart.org/content/medieval-the-bards-tale
- troubadour, “Fantasy Song Pack Volume 1”, CC0: https://opengameart.org/content/fantasy-song-pack-volume-1
- Elyvilon, “Nimueh's Gift”, CC-BY 4.0 (attribution required): https://opengameart.org/content/medieval-fantasy-ambient-music-nimuehs-gift

At this stage no additional audio is bundled. The project is prepared to switch among the six named slots as real, licensed files are added.
