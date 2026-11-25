# Ashes of the Crown — Version 37

## Companion personal storylines

- Added `CompanionStoryManager`, a pure Kotlin rules module for four individual companion arcs: Garrett Morn, Celeste, Mira, and Thomas.
- Each companion has three chapters with unique scenes, support/investigation choices, trust requirements, and bounded relationship rewards.
- Story choices advance only that companion's chapter state. The final chapter marks the arc complete; completed arcs cannot be advanced again.
- Story scenes are available only when the companion is physically available according to `NpcInteractionManager`.
- Personal moments and story chapters share the one-important-conversation-per-campaign-day limit.
- Every resolved chapter is recorded in the journal.

## Save compatibility

- Save schema advanced from 8 to 9.
- Added four independent progress keys: `story_companion_garrett`, `story_companion_celeste`, `story_companion_mira`, and `story_companion_thomas`.
- Schema 1–8 saves migrate additively; all new story stages begin at 0. Existing bonds, quests, inventory, and world state remain unchanged.
- New Game resets all four story arcs.

## Verification

- 14 pure Kotlin test mains passed, including the new companion story regression suite and existing 2,000-case differential game-day checks.
- Version 37 static integration checks passed.
- AndroidManifest.xml parsed successfully.
- Full Android/Compose build and device testing were not performed in this environment.
