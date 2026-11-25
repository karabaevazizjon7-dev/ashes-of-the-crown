# Architecture extraction progress

## Current state

- `MainActivity.kt` still owns the Compose UI, remembered game state, persistence orchestration, and gameplay actions.
- `GameRules.kt` now owns deterministic calculations that do not need Android or Compose APIs.
- The rules module is intentionally stateless: inputs are passed in and results are returned, so it can be tested independently.

## Extraction completed in working version 17

- Wound attack and incoming-damage modifiers.
- Armor calculation and guarded incoming damage.
- Defeat gold-loss calculation.
- Daily repeatable combat-reward eligibility.
- Army daily ration requirement and shortage-related soldier loss.
- Provincial daily revenue calculation.

## Next architecture steps

1. Introduce small data models for battle results and daily kingdom settlement results.
2. Move deterministic battle reward selection into the rules module.
3. Extract save-slot serialization and validation into a persistence component, preserving the current SharedPreferences format and migration behavior.
4. Extract day rollover processing into a domain component that returns a result for the UI state to apply.
5. Extract quest/action guards without changing the existing narrative content or save compatibility.

Do not rewrite the app from scratch. Keep changes incremental and preserve existing gameplay behavior unless a documented bug fix explicitly changes it.


## Extraction completed in working version 18

- Added `BattleRules.kt` as the dedicated pure combat-rules component.
- Moved wound modifiers, armor calculation, guarded damage, defeat gold-loss calculation and battle reward decisions out of the generic `GameRules` object.
- `BattleRules.rewardPlan` now returns one immutable reward plan (eligibility, XP, gold, reputation, daily-counter behavior and first-boss marker), so the Compose screen applies a result instead of re-implementing reward selection.
- `GameRules.kt` now focuses on kingdom/economy calculations: army ration requirements, shortage losses and provincial revenue.

## Next architecture steps

1. Extract the save-slot snapshot validation and migration rules into a pure persistence helper, leaving SharedPreferences and Activity recreation in the UI layer.
2. Extract the daily kingdom settlement into a deterministic result calculator.
3. Continue separating quest conditions and action guards from Compose callbacks.

## Lightweight rules checks

A dependency-free executable check is kept at `tools/RulesCheck.kt`. It can be compiled with the installed Kotlin compiler alongside `BattleRules.kt` and `GameRules.kt`; it is not an Android instrumentation test and does not replace the deferred end-to-end verification.


## Extraction completed in working version 19

- Added `RealmDayProcessor.kt`, a pure domain component that resolves one in-game day of army provisioning, shortage losses, guard morale loss, province loyalty pressure, unrest/revolt progression, revenue, steward payroll and unpaid-steward consequences.
- The Compose screen now builds a province snapshot, calls the processor once for each crossed day, and applies the returned state. Multi-day travel continues to process every day in order.
- Added `SaveDataRules.kt` for bounded fixed-size save-list parsing and daily-marker normalization. This prevents malformed comma-separated province values from shifting across province indices and prevents future-dated counters from locking daily actions.
- Manual save-slot validation now checks required campaign fields and rejects unsupported nested JSON values before clearing the active preferences.
- The main menu now exposes a persistent music toggle; battle events explicitly switch into and out of the combat music theme.
- Manual save-slot labels refresh immediately after a save, and the UI confirms the write instead of silently leaving stale slot metadata on screen.
- The pure rules test harness now includes 2,000 deterministic differential comparisons against the previous daily-settlement algorithm.

## Next architecture steps

1. Extract the JSON snapshot schema/type validation and migration table into a dedicated `SaveManager`/`SaveCodec`, leaving SharedPreferences writes and Activity recreation at the Android boundary.
2. Extract battle action orchestration (enemy turn, victory, defeat and reward application) into a `BattleManager` that returns state deltas.
3. Introduce a structured `GameState` model to replace the large set of unrelated Compose `remember` variables gradually, without rewriting the project wholesale.
4. Extract quest condition checks and one-time reward guards into a `QuestManager`.
5. Add focused automated tests for save migration and quest progression before the final Android build.

## Extraction completed in working version 20

- Added `SaveManager.kt` as a pure Kotlin persistence-domain component. It owns the save schema constant, required-key/type validation, numeric bounds, legacy schema handling and immutable normalized snapshot creation.
- Moved the large validation/range table out of `MainActivity.kt`. The Compose layer now parses Android `JSONObject` values, delegates validation, and applies the accepted primitive snapshot to SharedPreferences only after validation succeeds.
- New manual saves include a SHA-256 checksum. Existing slots without a checksum remain compatible; slots with a mismatched checksum are rejected before active campaign state is cleared.
- Loading preserves all manual slot payloads, labels and checksums, as well as the user-level music preference.
- Added pure Kotlin regression checks for validation, clamping, future-schema rejection and payload integrity.

### Remaining persistence architecture work

- SharedPreferences access and slot UI orchestration still live in `MainActivity.kt`; a platform-facing `SharedPreferencesSaveStore`/repository remains to be extracted.
- Save migration is currently compatibility-by-default for optional legacy keys. Explicit per-schema migration functions and backup/restore tests remain future work.

## Extraction completed in working version 21

- Added `SaveSlotStore.kt` as the Android persistence adapter for manual slots. It owns JSON transport, checksum verification, slot metadata, SharedPreferences commits and best-effort rollback after a failed commit.
- `MainActivity.kt` now delegates save/load slot operations and retains only campaign save orchestration, user feedback and Activity recreation.
- Save snapshots are validated and normalized before serialization. Slot loading validates the full payload before active preferences are changed.
- `SaveManager` now rejects fractional and non-finite values in integer fields rather than truncating them.

## Next architecture steps

1. Extract battle action orchestration (enemy turn, victory, defeat and reward application) into a state-delta component, keeping Compose state mutation at the UI boundary.
2. Extract quest completion checks and one-time reward claims into a `QuestManager` with explicit state inputs and outputs.
3. Begin a structured immutable `GameState` snapshot for one bounded subsystem at a time; do not replace all Compose state variables in one rewrite.
4. Add real Android/Compose compilation and manual-slot instrumentation coverage at the agreed final verification stage.

## Working version 22 — quest domain extraction

- Added `QuestManager.kt` as the first dedicated quest-domain component.
- The medicine-crate quest now uses explicit `MedicineQuestState`, `MedicineQuestAction` and `MedicineQuestTransition` values. UI code applies returned state/effects instead of duplicating quest mutation logic in multiple callbacks.
- Black Rose evidence, chapel-location and Seven Houses alliance gates are centralized as pure rules.
- This is an incremental extraction: `MainActivity.kt` still owns Compose rendering, campaign state and the final application of quest effects. A complete QuestManager migration is not yet done.

## Working version 23 — inventory domain extraction

- Added `InventoryManager.kt` as a pure rules module for item categories, unique/stackable insertion, removal, counts, lookup and atomic crafting.
- Replaced direct item-list mutations in the main game flow with a small Compose-state adapter that applies manager results. The UI still owns the observable list and persistence boundary.
- The inventory page now groups existing entries into visible categories and shows quantities for stackable items.
- The existing save representation is unchanged. The manager does not yet own equipment slots, market pricing, item-use effects or a complete item database; those remain future extraction work.

## Version 24 — combat/equipment extraction
`BattleManager` now owns deterministic combat calculations used by the Compose battle UI. `EquipmentManager` resolves the active weapon and armor from the legacy inventory list, preserving save compatibility while removing duplicated equipment-priority logic from combat UI.

## Version 25 — economy boundary and campaign snapshots

- `EconomyManager` owns deterministic economy formulas and immutable transactional actions.
- `GameRules` remains a compatibility facade while callers are migrated incrementally.
- `GameState` introduces an immutable campaign/realm snapshot boundary. Compose state remains in `MainActivity` until later migrations can be performed in smaller verified slices.
- The daily realm processor now consumes the `GameState.Realm` snapshot and its result is applied through the realm model.

## Version 26 additions
- `PlayerProgressManager.kt`: pure progression calculations independent of Compose.
- `JournalManager.kt`: bounded journal normalization and append behavior.
- `MainActivity.kt` delegates XP grants and journal appends to these modules. The screen still owns Compose state; full GameState migration remains future work.

## Version 28 — world and travel boundary

- Added `WorldManager.kt` as a pure domain module for canonical location names, the location catalogue, story unlock rules and travel-plan validation.
- Both map surfaces now call the same `travelToLocation` adapter in `MainActivity.kt`; travel time is no longer bypassed by the compact `MapPlace` list.
- The manager returns typed ready/rejected results. Compose still owns live campaign state, applies the selected destination, advances world time and persists the result.
- Dangerous-road metadata is descriptive only in this pass; random encounters are not yet implemented. Save schema remains 7.

## Version 29 — road graph and route planning

- Added `TravelManager.kt` as a pure domain module with a bidirectional road graph and shortest-time route resolution.
- The route result includes all intermediate locations, total travel hours and dangerous road-segment count.
- `MainActivity` now combines story-unlock validation from `WorldManager` with route calculation from `TravelManager`; world time and journal output use the resolved route rather than a direct destination hop.
- The current UI callback signature remains compatible, but its legacy requested-hours value no longer overrides road-network travel time.
- Random encounters, NPC schedules and faction simulation are not implemented yet. Save schema remains 7.

## Version 30 — RandomEncounterManager

Road-trip event selection now lives in a pure Kotlin module. The UI applies typed outcomes, while battle initiation is delegated to the existing battle entry point. Encounter resolution is deterministic for a given route/day/departure-hour/encounter-count tuple, preventing callback retries from rerolling an event. No persistence schema change was required.

## Version 31 architecture update
- `NpcScheduleManager` now owns the deterministic schedule catalogue and time-window evaluation used by the Life page.
- `FactionManager` owns normalization and presentation of the four pre-existing faction reputation tracks; persistence and actual reputation mutations remain in the current game state/UI layer.
- These are focused extractions, not yet a complete world simulation or event-driven faction engine.

## Version 32 — daily world-event domain boundary

- Added `WorldEventsManager` as a pure deterministic domain service. It resolves a typed daily event from a campaign snapshot and returns bounded effect deltas without mutating UI state.
- Integrated the manager into the existing daily-upkeep loop. Every crossed campaign day gets one event resolution; retries without a new upkeep day do not repeat event rewards.
- The Compose boundary applies the result to existing campaign variables, uses `FactionManager` for reputation clamping, records the event through `JournalManager`, and exposes the latest report through the existing `worldEvent` field.
- No new preference key or save migration was introduced; schema remains 7. This is a daily news/effect system, not a full autonomous faction AI. NPC timetable movement is introduced in Version 33 as deterministic clock-derived itineraries, not autonomous pathfinding.

## Version 33 — NPC itineraries
- Expanded `NpcScheduleManager` from one fixed time window per character to a daily itinerary with multiple location/time slices.
- The current position of Garrett, Celeste, Mira and Thomas is derived from the campaign hour. Each slice distinguishes physical presence from conversation availability.
- Added a complete four-character roster and a next-transition calculation; the Life page now renders both.
- This is deterministic timetable movement, not autonomous pathfinding or NPC AI. No new save data is required; schema remains 7.

## Version 34 — schedule-aware dialogue boundary
- Added `NpcInteractionManager` as a pure domain gate over the existing NPC timetable and canonical world locations.
- Dialogue topic controls are disabled when the selected NPC is elsewhere or busy; the click handler independently revalidates before changing trust or journal state.
- Typed interaction results distinguish `Away` from `Busy`, allowing the UI to explain whether the player must travel or wait.
- Duke Reynar is not yet in the timetable catalogue; until his schedule is modeled, the dialogue gate conservatively permits face-to-face interaction only in the Royal Castle.
- No new persistence keys; schema remains 7.

## Version 35 — companion relationship domain

- `CompanionManager` now owns pure rules for character-specific personal moments and response outcomes in addition to expedition outcomes.
- The Compose boundary stores separate bond values for the four companions and applies manager results. One personal conversation is permitted per campaign day and is journaled.
- Save schema advanced from 7 to 8. The migration maps the legacy single bond to the companion selected in the source save and initializes other missing bonds to neutral; existing explicit values are retained.
- This is a first relationship-system layer, not yet a full companion quest graph, inter-companion conflict simulation, or autonomous companion AI.
- Verification covers pure Kotlin rules and static integration only; Android compilation and device testing remain outstanding.

## Version 36 — derived faction agenda
- `FactionAgendaManager` is a pure derived rules layer. It consumes the four bounded faction standings and returns sorted political pressure points; it does not own persistence or mutate campaign state.
- The Life page is the presentation boundary. This keeps political interpretation testable independently from Compose and avoids a schema bump for information that can be reconstructed from existing reputation values.

## Version 38 — Social relationship layer

`CompanionRelationshipManager` is a pure Kotlin rules module, separate from `CompanionManager` (hero-to-companion bonds) and `CompanionStoryManager` (individual story progress). Six pair affinities are persisted by `MainActivity` and validated/migrated by `SaveManager` schema 10. Pair scenes use `NpcInteractionManager` to require both participants to be present and free. UI remains in the Activity; this update does not claim the larger UI extraction is complete.

## Version 39 — cooperative companion events

`CompanionTeamEventManager` adds six deterministic one-time scenes, one for each pair in the companion roster. It consumes the existing pair-affinity and hero-to-companion bond values and returns bounded results; it does not own storage. `MainActivity` remains the persistence/presentation boundary and stores completion in a six-bit mask plus a daily guard. Both participants are gated through `NpcInteractionManager` at render time and revalidated on click. Save schema 11 migrates older saves by adding neutral event state only. This is a finite authored event layer, not a general-purpose branching quest graph or autonomous companion simulation.

## Version 41 — crisis consequence boundary

`CrisisConsequenceManager` is a pure rules layer. It receives previous/current crisis counters and province names, emits one-time transition outcomes, and exposes the explicit adjacency map. `MainActivity` remains the integration boundary that applies reputation and loyalty deltas through existing bounded state paths and writes journal/report text. No duplicate persistence state was added.

- V42: `RealmPriorityManager` is a pure, read-only aggregation layer for economic, military, provincial and faction state, rendered in the Esterval overview.


## Version 43 — narrative action boundary
Moved deterministic narrative choice validation and resource/reputation calculation into `GameActionRules`. `MainActivity` now applies the immutable result and retains UI, inventory, journal, and save responsibilities. Save schema unchanged at 11. This is an incremental extraction, not a full UI architecture rewrite.

## Version 44 — world clock boundary

`WorldClockManager` owns pure clock arithmetic and phase/message selection. It returns an immutable `WorldClockAdvance` including the number of daily upkeep cycles to resolve. The UI layer consumes that plan and continues to orchestrate the already-established deterministic settlement sequence; this keeps economic, crisis, faction, and world-event side effects in their proven order while shrinking the clock-specific responsibility of `MainActivity`.

## V55 — current-schema save completeness

Schema-11 manual saves are now required to contain every fixed key emitted by `MainActivity.save()`. This prevents a partial current-version slot from passing validation and silently resetting omitted progress after `SaveSlotStore` clears active preferences. Legacy schema-10 migration remains supported. See `VERSION_55_CHANGELOG.md` and `VERSION_55_VERIFICATION.md`.

## V56 — manual save-slot integrity policy

Current-schema manual saves now require a non-empty SHA-256 hash. Missing hashes remain accepted only for legacy schema 10 and older. Existing hash verification remains strict. The rule runs after detached snapshot validation but before preference replacement, preserving the no-mutation-on-rejection guarantee. Schema stays at 11.

## V57 — save schema boundary validation

The schema marker is now validated as a wide integer before conversion to `Int`. This closes an integer-wrap edge case in manual-save validation where a large untrusted marker could narrow to the current schema number. The regression suite explicitly tests the wraparound value `4_294_967_307L`. No save schema or game-state contract changed.
