# Ashes of the Crown — Part I progress

Working baseline: Alpha 27 source project.

## Implemented in the first foundation pass

1. Replaced the partial New Game reset with a reset covering the current persisted gameplay state: story/quest progress, relationships, personal and social flags, dungeons, expeditions, Council, companions, frontier, army, treasury, provinces, dialogue progress, combat, XP/skills, location/time, journal and inventory. The music preference and manual save slots are intentionally preserved.
2. Added a save schema marker (`save_schema_version = 2`). Slot loading now validates the slot number and JSON payload, checks the types of known fields, rejects empty/incomplete snapshots and saves from a newer schema, and reports malformed saves without clearing the current progress. Existing unversioned Alpha 27 slot snapshots are treated as schema 1 and accepted.
3. Split one-off Celeste/Reynar relationship scenes into a separate `social_event_flags` value so they no longer consume the four personal-story bits.
4. Added separate flags for three one-time social actions in the Relationships page.
5. Dialogue progress and trust are now stored per NPC. Each NPC's three conversation topics can award relationship progress only once; the old reset-conversation button that enabled repeat farming was removed. Switching characters restores that character's own dialogue progress.
6. Centralized all direct XP rewards through `grantExperience()`, including combat, frontier, quests, dungeons and army training. Level-ups now resolve consistently for all sources.
7. Enforced the displayed maximum of 10 for Power and Guard skill upgrades.
8. Made crafted Reinforced Chainmail contribute +5 defense, above the regular Chainmail's +3, and updated the combat equipment description.
9. Added per-province daily tax collection tracking. A province cannot be taxed repeatedly on the same in-game day.
10. Prevented spending gold to develop a province already at maximum prosperity.

## Deliberately not done yet

- No Android build or device test was run, in accordance with the agreed plan to perform checks at the end of Part III.
- The original Alpha 27 source folder remains untouched; all work is in this separate working copy.
- Legacy saves cannot reveal whether old overlapping personal-quest bits came from a character quest or a relationship scene. New progress is separated going forward; a dedicated migration decision is still needed for ambiguous old flags.
- Other repeatable rewards, quest completion gates, combat reward farming, economy upkeep, map unification and the remaining Part I architecture work are still pending.

## Second foundation pass

11. Companion expeditions now award their companion-specific rewards only once per in-game day. The day marker is persisted, so leaving/reopening the app does not reset the limit. The next expedition becomes available on the next in-game day.
12. The missing-medicines quest now requires an actual port search to discover and add the medicine crate. Returning to Mira before finding it no longer completes the quest or grants the reward. Handing the crate over consumes the quest item and closes the quest.
13. The two one-off social story callbacks (Celeste's private confession and Reynar's political secret) now check their event bits before applying relationship/influence rewards, preventing repeated taps from farming rewards.
14. Save schema advanced to version 3 for the new persisted quest and expedition fields. The slot loader accepts schema 1/2 legacy saves and schema 3, rejects newer schemas, and validates the new field types.

## Second-pass boundaries

- No APK/Android build or device test was run; final verification remains deferred until the end of Part III.
- These are source-level changes only. The archive is a working source snapshot, not an installable APK.

## Third foundation pass

15. Boss reward protection: the Ancient Knight can be defeated for its unique reward only once. The first victory grants the unique `Реликвия Древнего рыцаря`, 90 XP and 25 gold; the boss is then disabled in the manual battle list and removed from random encounters. The victory flag is persisted and reset by New Game.
16. Travel investigations: the clue and +1 influence reward at the Northern Outpost, Crow Village and Forest Road are now one-time per location. Each location has its own persisted flag; the clue is no longer repeatedly farmable by tapping the interaction.
17. Council repeat exploit: removed the endless `Созвать новый совет` reset after all three decisions. The completed council remains completed until a future story chapter provides a new council cycle.
18. Save schema advanced to version 4. The new boss and exploration flags are saved, validated during slot loading, and reset for a new game. Schema 1–3 legacy saves remain accepted with defaults for the new fields.

## Third-pass boundaries

- No Android build, APK assembly or device test was run. Final verification remains deferred until the end of Part III, as agreed.
- The original Alpha 27 source and the previous v2 working archive were not modified; this v3 working snapshot contains the latest source changes.


## Fourth foundation pass

19. Enforced the intended 100-unit cap for the royal army and provisions. Legacy values are clamped when loaded; recruitment and provisioning actions now refuse overflow rather than silently exceeding the UI scale.
20. Recruitment now requires room for a complete five-soldier batch, so the army cannot overshoot its 100-soldier limit.
21. Army training is disabled once Guard reaches 100, preventing provisions and time from being spent on a capped stat.
22. Provision purchases are disabled when fewer than 10 storage spaces remain; purchases cannot waste gold by overflowing the 100-provision storage limit.

## Fourth-pass boundaries

- No Android build, APK assembly or device test was run. As agreed, those checks remain scheduled for the end of Part III.
- This pass changes only existing state bounds, action guards and the army/provision UI; it adds no new save fields and therefore keeps save schema version 4.

## Fifth foundation pass — main-story finale

23. Replaced the three finale choices that all returned to scene 10 with three distinct epilogue destinations. The player now reaches a unique ending based on whether they open the archive, seal it while gathering allies, or let Celeste enter first.
24. Added three terminal epilogue scenes with no further choices, so the finale no longer loops back into the middle of Chapter III or repeats the same story section.
25. Existing saves remain compatible: the original 18 scene indices are unchanged, and the three new epilogues are appended as indices 18–20. No new save fields were introduced; schema remains version 4.

## Fifth-pass boundaries

- Source-level change only. No Android/Gradle build, APK assembly, emulator run or device test was performed. As agreed, final verification remains deferred until the end of Part III.
- The three epilogue texts are a first complete ending pass; later Part II narrative expansion can add consequences, post-ending content or further endings without reusing the old scene loop.


## Sixth foundation pass — explicit story rewards and paid-choice validation

26. Removed item grants inferred from searching narrative outcome text. Story choices now declare their item reward explicitly through the `GameChoice.itemReward` field. The copper key and port manifest are granted only by their corresponding choices.
27. Added a shared affordability check to story-choice handling. Any choice with a negative gold value now refuses to execute if the player cannot pay its full cost; stats, inventory and scene progression remain unchanged. This closes the free-branch exploit caused by clamping negative gold to zero.
28. Explicit story rewards are not duplicated if the item is already present in inventory. No new persisted state was introduced; save schema remains version 4.

## Sixth-pass boundaries

- Source-level change only. No Android/Gradle build, APK assembly, emulator run or device test was performed. Final verification remains deferred until the end of Part III.
- Existing Alpha 27 source and the previous v5 working snapshot remain untouched.

## Seventh foundation pass — canonical world map and location names

29. Unified the illustrated realm map with the canonical location names already used by the world page, story events and quests. The harbor is now consistently named `Порт Веймар`; the duplicate `Порт Эстервала` map node has been removed.
30. Added the existing northern locations and the Old Chapel to the illustrated realm map, so the main travel map now exposes the same set of named destinations as the world-location system.
31. Added visible locked-state treatment to map nodes and aligned unlock conditions with the existing world page: northern locations and Valden Fortress open from story progress 5, the harbor from story progress 7, and the Old Chapel from Rose quest stage 1.
32. Legacy saves that still store `Порт Эстервала` are normalized to `Порт Веймар` when the game state is initialized. This preserves the old save while ensuring harbor events, quest checks and music/location presentation use one canonical location string.

## Seventh-pass boundaries

- Source-level map and location consistency changes only. No Gradle/Android build, APK assembly, emulator run or device test was performed; final verification remains deferred until the end of Part III.
- No new persisted fields were introduced; save schema remains version 4.

## Eighth foundation pass — heavy-attack risk/reward balance

33. Heavy attacks now expose the player to a stronger enemy counterattack: the enemy's next raw hit receives +4 damage before armor and guarding mitigation. Quick attacks retain the standard counterattack. If a heavy attack defeats the enemy, no counterattack occurs.
34. Updated the heavy-attack button label and combat log to communicate the added risk. No new state or save fields were introduced; save schema remains version 4.

## Eighth-pass boundaries

- This is a source-level combat-balance adjustment only. No Android/Gradle build, APK assembly, emulator run or device test was performed.
- The previous v7 working snapshot and original Alpha 27 remain unchanged.

## Ninth foundation pass — one-time investigation rewards and paid-action guards

35. Added a one-time investigation flag for the Old Bridge. Its clue and +1 influence reward can no longer be farmed by repeatedly tapping the interaction; the flag is stored in the existing `world_investigation_flags` integer and legacy saves default it to unset.
36. Made Garrett's castle-security conversation and Celeste's ship-rumor conversation one-time rewards. Their relationship increases and memory progress no longer repeat on every visit. These use unused bits in the existing `social_action_flags` field, so no save-schema migration is required.
37. The port manifest purchased from Thomas is now a unique evidence item. The player cannot repeatedly buy the same clue to farm merchant reputation or duplicate the item.
38. Fixed the Council's grain-store decision: it now requires 3 gold before advancing the meeting, granting reputation, or applying the political result. Insufficient funds leave the meeting stage unchanged.
39. Fixed both paid northern campaign decisions. The open-road route requires 4 gold and the immediate assault requires 5 gold; neither route can advance for free when the treasury is short. The corresponding buttons now display their costs.
40. Clamped the newly affected Guard and Reputation rewards to their intended 100-point upper bounds.

## Ninth-pass boundaries

- No new preference keys were added. Existing `social_action_flags` and `world_investigation_flags` are reused; save schema remains version 4 and old saves remain structurally compatible.
- No Android/Gradle build, APK assembly, emulator run, or device test was performed. Full verification remains deferred until the end of Part III.

## Tenth foundation pass — unique quest artefacts and evidence

41. Added a single `addUniqueQuestItem()` helper for non-consumable story evidence, quest artefacts and one-time rewards. It checks the inventory before insertion, preventing duplicate copies if a reward callback is accidentally triggered again or a future branch reuses the same item.
42. Applied the helper to the Ancient Knight relic, northern campaign evidence and banners, companion evidence letter, dungeon artefacts, Old Chapel seal, Council sign, Mira's trade token and Celeste's cipher fragment. Consumables and stackable crafting materials remain unchanged.
43. No new preference keys or save fields were introduced; save schema remains version 4 and legacy saves remain structurally compatible.

## Tenth-pass boundaries

- Source-level inventory/reward hardening only. No Android/Gradle build, APK assembly, emulator run or device test was performed. Full verification remains deferred until the end of Part III.
- This helper prevents duplicate unique artefacts; it does not change repeatable combat XP/gold or consumable stack rules, which require a separate balance decision.

## Eleventh foundation pass — daily army upkeep and supply consequences

44. Added a persisted `lastUpkeepDay` marker and advanced the save schema to version 5. Slot loading accepts legacy schemas 1–4, defaults their upkeep marker to the saved current day (so old saves are not charged retroactively), and rejects schemas newer than 5.
45. The royal army now consumes provisions automatically when a new in-game day begins. Daily consumption is 1 ration per 10 soldiers, rounded up (24 soldiers consume 3; 100 soldiers consume 10). Multi-day time advances process each crossed day separately.
46. If provisions are insufficient at the day boundary, remaining provisions are exhausted, soldiers desert or are lost according to the shortage, Guard trust falls, and loyalty falls in each province. The report and journal explain the shortage and its consequences.
47. Added the daily ration requirement to both the economy and army screens, so the player can see the expected upkeep before advancing time or recruiting more troops.
48. New Game resets the upkeep day marker; all upkeep changes are included in the existing save snapshot. No new Android permissions or external dependencies were added.

## Eleventh-pass boundaries

- Source-level implementation only. No Android/Gradle compilation, APK assembly, emulator run or device test was performed, in accordance with the agreed plan to defer full verification until the end of Part III.
- Save schema is now version 5. Older Alpha 27 working saves (schemas 1–4) remain loadable; their first upkeep charge begins on the next newly entered in-game day.


## Twelfth foundation pass — automatic provincial revenue and steward payroll

49. Replaced the manual once-per-day provincial income claim with automatic revenue processing at each in-game day rollover. The same persisted day progression used by army upkeep processes every crossed day, including multi-day travel.
50. Daily provincial revenue now reflects prosperity and loyalty: high-loyalty provinces receive a modest bonus, while provinces below 25 loyalty suffer a modest revenue reduction.
51. Appointed stewards now receive one gold of daily wages each. Paid stewards improve their province's loyalty; if the treasury cannot cover all wages, unpaid stewards' provinces lose loyalty. Treasury income and payroll are applied without allowing the gold balance to become negative.
52. Updated the Provinces screen to show estimated gross revenue, steward wages and net daily income. The report and journal record automatic daily settlements. Existing `a13_income_day` and `a14_upkeep_day` markers are reused; save schema remains version 5 and no new persisted fields are introduced.

## Twelfth-pass boundaries

- Source-level economy update only. No Android/Gradle compilation, APK assembly, emulator run or device test was performed. The agreed full verification remains deferred until the end of Part III.
- The old manual provincial-income button was removed because revenue is now credited automatically at day rollover. Per-province tax collection remains a separate, player-triggered action with its existing daily limit.

## Thirteenth foundation pass — provincial unrest and revolt management

53. Added persisted per-province crisis counters. Loyalty below 20 for three consecutive processed in-game days escalates from unrest to open revolt; loyalty recovery to 30 or higher gradually reduces the counter. Existing saves default to stable provinces.
54. Open revolt suspends that province's automatic daily revenue and manual tax collection, and reduces its prosperity by one per affected day. Other provinces continue operating normally.
55. Added a province-screen status for stable, unrest and revolt states, including the escalation countdown. The player can resolve an active crisis by spending either 12 gold on relief or 4 influence on negotiations; resolution restores loyalty and clears the crisis counter.
56. Save schema advanced to version 6. The new `a15_crisis_days` field is included in slot snapshots and New Game reset. Slot loading accepts schemas 1–5 and schema 6, and rejects future schemas.

## Thirteenth-pass boundaries

- This is a source-level kingdom-management feature. No Android/Gradle compilation, APK assembly, emulator run or device test was performed; full verification remains deferred until the end of Part III.
- Crisis progression is tied to the existing in-game day rollover, so multi-day travel processes each crisis day in sequence. Existing saves start with no active unrest unless the new system has already recorded it.


## Fourteenth foundation pass — provincial administration controls

57. Added a steward dismissal action. Dismissing a steward stops future daily wages, reduces local loyalty by 3, records the action in the journal, and does not refund the influence originally spent on appointment.
58. Split provincial crisis resolution into two explicit choices: emergency relief costs 12 gold and restores 25 loyalty plus 2 prosperity; negotiations cost 4 influence and restore 20 loyalty. The player can now deliberately choose which resource to spend instead of the old automatic gold-first behavior.
59. Improved crisis recovery: provinces at 25 loyalty or higher now gradually reduce their unrest counter on subsequent processed days, avoiding a deadlock where loyalty between 25 and 29 could never calm unrest.
60. Corrected the province income forecast so it excludes provinces in open revolt, matching the actual daily settlement calculation.
61. No new persisted fields were introduced; save schema remains version 6 and existing saves remain structurally compatible.

## Fourteenth-pass boundaries

- Source-level province-management update only. No Android/Gradle compilation, APK assembly, emulator run or device test was performed. Full verification remains deferred until the end of Part III, as agreed.
- This pass changes only existing steward, crisis and forecast behavior; it does not introduce a new political-event subsystem.

## Fifteenth foundation pass — defeat consequences and daily combat reward limits

62. Reworked defeat handling. A knockout now records a defeat, deducts 10% of current gold with a minimum loss of 3 and a maximum of 10 (never more than the player owns), restores the hero to 35 HP, and applies a temporary wound through the next in-game day.
63. While wounded, player attacks deal 3 less damage and enemy raw attacks deal 2 additional damage before armor and guarding mitigation. The combat screen displays the wound state and its duration; the wound expires naturally when the next in-game day begins.
64. Added a daily reward limit for repeatable ordinary encounters: the first four ordinary victories per in-game day grant their normal XP, gold and reputation. Further victories still count in the battle record but grant no repeatable XP, gold or reputation. The unique first Ancient Knight victory remains separately rewarded.
65. Replaced the generic repeatable battle trophy with one unique trophy per enemy type, preventing repeated victories from stacking identical evidence items.
66. Added persisted defeat statistics, daily reward counters and wound expiry day. Save schema advanced to version 7; slot loading accepts schemas 1–6 with safe defaults for the new fields and rejects schemas newer than 7. New Game resets all new combat state.

## Fifteenth-pass boundaries

- Source-level combat and save-schema update only. No Android/Gradle compilation, APK assembly, emulator run or device test was performed. As agreed, full verification remains deferred until the end of Part III.
- Daily limits apply to repeatable ordinary encounters; they do not block combat itself or remove victory records. The Ancient Knight's first unique reward is not suppressed by the ordinary encounter limit.

## Sixteenth foundation pass — quest integrity and save-state normalization

67. Closed a repeatable reward exploit in Garrett's night-shipment handoff: the port manifest is now consumed when submitted, the +7 Guard / +1 Reputation reward is granted once, and a persisted bit in the existing `social_action_flags` prevents a second handoff. The flag is included in the existing save snapshot and reset with New Game; save schema remains version 7.
68. Closed repeatable merchant-reputation farming through Mira's unsigned-letter purchase. The letter is now a one-time unique evidence purchase; the first purchase costs 3 gold and grants +3 Merchant reputation, while later attempts are disabled. This uses another free bit in `social_action_flags` and adds no save field.
69. Hardened quest item handling: the medicine crate, empty cargo tag, Black Rose sealed letter, Garrett's veteran note and Thomas's night-cargo list now use the unique-item helper. Returning the medicine crate removes all duplicate copies from legacy inventories before issuing the one-time completion reward.
70. Added bounded normalization when loading structurally valid save slots. Story indices, quest stages/paths, faction stats, HP, gold, skill levels, dungeon progress, army/provisions, battle counters and other bounded numeric fields are clamped to their legal ranges; per-character dialogue stages/trust are normalized as well. Malformed field types are still rejected before preferences are replaced.
71. No new persisted keys were introduced. Save schema remains version 7; the new one-time action markers are stored in the already persisted `social_action_flags` integer. New Game already resets that integer, so the markers reset with the rest of the campaign.

## Sixteenth-pass boundaries

- Source-level quest/economy/save hardening only. No Gradle/Kotlin compilation, APK assembly, emulator run or device test was performed. Full verification remains deferred until the end of Part III.
- Numeric normalization applies when loading a manual save slot. It does not silently rewrite the player's current live state during ordinary play.

## Seventeenth foundation pass — first gameplay-rules module extraction

72. Created `app/src/main/java/com/ashes/crown/GameRules.kt`, a pure Kotlin rules module with no Android or Compose dependencies. It centralizes calculations for wound penalties, armor, guarded incoming damage, defeat gold loss, daily battle-reward eligibility, army ration requirements, shortage-related soldier losses, and provincial daily revenue.
73. Refactored `MainActivity.kt` to call `GameRules` for those calculations. The Compose screen still owns UI state and action orchestration, while deterministic rule calculations now live in a separate source file. This is the first incremental extraction toward the planned `GameState` / `GameEngine` / `GameRules` separation; it is not yet a complete architectural split.
74. Kept the existing save schema at version 7. No persisted keys, preference names, resource files, dependencies, or Android permissions were added or changed.
75. Compiled `GameRules.kt` independently with the installed Kotlin compiler and ran a temporary set of checks covering armor, wounds, guarded damage, defeat losses, reward limits, army rations, shortage losses, and province revenue; all checks passed. The temporary check harness was not added to the app project.

## Seventeenth-pass boundaries

- This pass extracts deterministic rules and verifies that standalone file. It does not constitute an Android app build, Compose/Kotlin project compilation, APK assembly, emulator run, or device test. Those remain deferred until the end of Part III as agreed.
- The existing `MainActivity.kt` remains large; only pure calculations were extracted to reduce risk. The next architecture passes should move persistence, day-processing orchestration, battle actions, and quest actions incrementally rather than attempting a full rewrite.


## Eighteenth foundation pass — dedicated combat rules and reward planning

76. Added `BattleRules.kt` as a dedicated, Android-free combat rules component. Wound modifiers, armor, guarded damage and defeat gold-loss calculations now live there instead of the general `GameRules` object.
77. Moved battle reward selection into `BattleRules.rewardPlan`. The function returns a single immutable plan containing reward eligibility, XP, gold, reputation, whether the daily counter advances, and whether this is the first unique boss victory.
78. Updated `MainActivity.kt` to apply the returned reward plan rather than duplicating the reward-selection logic in the Compose screen. Existing reward amounts and daily-limit behavior are preserved: ordinary encounters pay up to four times per day; the first Ancient Knight victory pays its unique reward; later boss victories do not pay repeatable rewards.
79. Narrowed `GameRules.kt` to kingdom/economy calculations (daily army rations, shortage losses and provincial revenue), giving the codebase clearer responsibility boundaries without changing save keys or schema.

## Eighteenth-pass boundaries

- This is a source refactor plus a deterministic rules extraction. No Android/Gradle build, APK assembly, emulator run or device test was performed.
- Save schema remains version 7; no persisted fields or story content were changed.

### Rules checks for the seventeenth pass

- Compiled `BattleRules.kt` and `GameRules.kt` with the standalone Kotlin compiler and ran deterministic checks for armor, guarded damage, wound modifiers, defeat loss, ordinary reward cap, first/repeat boss rewards, army ration calculation, shortage losses and provincial revenue. All checks passed.
- This does not compile `MainActivity.kt` or the Android application; full Android verification remains deferred.

## Nineteenth foundation pass — deterministic realm-day processing and save hardening

80. Extracted the per-day army upkeep, province unrest progression, revolt prosperity loss, automatic provincial revenue and steward payroll from the Compose screen into `RealmDayProcessor.kt`. The processor takes an immutable snapshot and returns a complete result; the UI layer now only applies that result to remembered state.
81. Added `SaveDataRules.kt` for fixed-size, bounded parsing of province save arrays. Invalid or missing tokens now fall back at their own index instead of shifting later values into the wrong province. Prosperity and loyalty are clamped to 0–100, steward flags to 0–1, crisis counters to 0–3, and province tax-day markers to the current campaign day.
82. Normalized all persisted daily markers against the loaded campaign day (army upkeep, provincial income, tax collection, companion expedition and battle rewards). Future markers in malformed/legacy saves can no longer lock daily actions until the calendar catches up.
83. Strengthened manual-slot validation: slots outside 1–3 are ignored; core campaign fields must exist; unsupported nested JSON values are rejected before preferences are cleared; the existing save is left untouched when validation fails. Save schema remains version 7.
84. Fixed a duplicate `save()` call in the unaffordable story-choice path. Added a guard against starting/replacing an encounter while another enemy is alive. Battle start now switches the music event to combat, and victory/defeat restore a non-combat event so battle music does not remain active indefinitely.
85. Added a persistent music on/off control to the main menu. Corrected in-app version labels and the README's top-level project identity to match the actual Gradle baseline (`6.0.0-alpha27`, `versionCode 83`), while preserving historical changelog entries.
86. Kept the existing save schema at version 7; no new save keys, Android permissions, external dependencies or resource identifiers were introduced.
87. Compiled the pure Kotlin rules modules (`GameRules`, `BattleRules`, `SaveDataRules`, `RealmDayProcessor`) with the installed Kotlin compiler and ran deterministic checks for combat rewards, bounds, malformed province arrays, daily marker normalization, supplied/shortage days, revolt revenue suspension and unpaid stewards. In addition, 2,000 deterministic differential cases compared the extracted daily processor against the previous in-screen settlement algorithm; all checks passed.
88. Fixed a save-menu refresh issue: after saving a manual slot, the slot label now refreshes immediately and a confirmation toast is shown. The menu's music status text now accurately says progress is saved after game actions rather than claiming an unimplemented lifecycle autosave.

## Nineteenth-pass boundaries

- This is a source-level refactor and deterministic rules test, not an Android app build. `MainActivity.kt` and Compose integration were not compiled in this pass; no APK, emulator or device test was run. Full Android verification remains deferred until the end of Part III, as agreed.
- The original Alpha 27 project remains untouched. The archive is a working source snapshot, not an installable APK.

## Version 20 — save pipeline extraction and integrity protection

42. Extracted manual-slot schema validation, primitive-type validation, required-field checks and numeric normalization from `MainActivity.kt` into the pure Kotlin `SaveManager.kt` component. The Android boundary now converts the parsed JSON object to primitive values, asks the manager to validate it, and only then mutates SharedPreferences.
43. Added SHA-256 integrity checks for newly written manual save slots. If a slot has a checksum and its payload no longer matches, loading is rejected before the active campaign preferences are cleared. Legacy slots without checksums remain loadable.
44. Loading preserves all three slot payloads, labels and checksums while replacing campaign state. Music preference remains outside the campaign snapshot and is preserved.
45. Kept save schema at version 7: this is an implementation-level extraction and integrity addition, not a change to the campaign data schema. Old unversioned and schema 1–7 saves continue through the existing compatibility path; schemas newer than 7 are rejected.
46. Added automated pure-Kotlin checks for required fields, malformed primitive types, nested values, future schemas, legacy defaults, range clamping, input immutability and checksum matching/mismatch.

## Version 20 verification boundary

- Compiled the pure Kotlin domain/persistence modules and ran `RulesCheck.kt`; all checks passed, including the existing 2,000 deterministic realm-day differential cases.
- This did not compile the Android app or Compose integration. No APK, emulator or device test was run; those remain scheduled for the end of Part III.
- `SaveManager.kt` owns validation, normalization and hashing. SharedPreferences I/O, Toast messages and Activity recreation still live in `MainActivity.kt` and are not yet extracted into a platform storage adapter.
47. Corrected the Android GitHub Actions workflow to match the actual repository layout: it now verifies `settings.gradle.kts`, `app/build.gradle.kts` and the manifest at the root, then builds `:app:assembleDebug` directly. Removed the obsolete binary-part concatenation and nonexistent `ashes_v2` working directory. Updated README build instructions and artifact naming.

## Twenty-first foundation pass — manual save-slot storage extraction

89. Added `SaveSlotStore.kt` as the Android storage adapter for the three manual campaign slots. It now owns slot JSON serialization/parsing, checksum verification, validation hand-off, slot metadata preservation, SharedPreferences commit and rollback handling.
90. Reduced `MainActivity.kt` save-slot callbacks to orchestration: the UI saves the current campaign, asks `SaveSlotStore` to write or load a slot, displays the returned status, and recreates the Activity only after a successful load.
91. Manual slot writes now validate the detached campaign snapshot before storing it and serialize the normalized current-schema snapshot. A failed write restores the previous contents of that slot.
92. Slot loading validates the checksum and complete payload before touching active preferences. The store captures the previous preferences, applies the validated campaign and preserved music/slot metadata in one synchronous commit, and attempts to restore the previous preferences if that commit reports failure.
93. Hardened `SaveManager` against fractional and non-finite values in integer fields, schema markers and per-character dialogue counters. Such values are rejected rather than silently truncated during normalization.
94. Added regression cases for fractional schema versions, fractional gold and non-finite numeric values. Save schema remains version 7; this is a persistence-boundary refactor and validation hardening, not a campaign schema change.

## Twenty-first-pass boundaries

- Pure Kotlin domain and save validation modules were compiled and their regression harness was executed. The Android-facing `SaveSlotStore.kt` was type-checked against minimal compile-time Android/JSON API stubs; this is not a substitute for compiling against the real Android SDK.
- No Android Gradle build, APK assembly, emulator launch or device test was run. Full app integration remains scheduled for the end of Part III.
- The original Alpha 27 project and working version 20 remain untouched. This version 21 snapshot is based on version 20.

## Twenty-second foundation pass — quest rules extraction and progression guards

76. Added `QuestManager.kt`, a pure Kotlin quest-domain component with an explicit transition model for the medicine-crate quest. Accepting, discovering the crate and completing the quest are now separate actions with validated preconditions.
77. Refactored the market and port interactions to apply `MedicineQuestTransition` results rather than directly mutating quest stage, crate state, gold, reputation and inventory in multiple UI callbacks. Completion consumes the crate, grants 8 gold and +4 commoner reputation, and awards the empty cargo tag exactly once.
78. Invalid medicine-quest actions now leave quest state and rewards unchanged and return a player-facing explanation. A completed quest cannot be completed again; the crate cannot be found before accepting the quest or duplicated by repeating the port interaction.
79. Moved the Black Rose evidence gate, old-chapel location gate, Seven Houses entry gate, alliance-selection gate and alliance-finalization gate into the same domain component. UI callbacks now consult these rules before applying their existing story consequences.
80. No save fields or schema changes were introduced. The medicine quest continues to use the existing `quest_market` and `quest_medicine_crate_found` preferences, so schema remains version 7.

## Twenty-second-pass verification boundaries

- Compiled `QuestManager.kt` with `QuestRulesCheck.kt` and ran the deterministic assertions for quest acceptance, evidence discovery, premature/repeated completion, item consumption, reward amounts, Black Rose evidence gates, chapel location, council entry and alliance finalization; all passed.
- Recompiled and ran the existing pure Kotlin rules harness: combat, economy, save validation/integrity, normalization and 2,000 realm-day differential cases passed.
- Ran Kotlin compiler parsing over the modified `MainActivity.kt` together with `QuestManager.kt`; no syntax/parser diagnostics were found. Full type resolution is not possible without the Android/Compose dependency classpath.
- No Android Gradle build, APK assembly, emulator launch or device test was performed. This remains deferred to the end of Part III as agreed.

## Twenty-third foundation pass — inventory domain extraction and categorized inventory

95. Added `InventoryManager.kt` as a pure Kotlin inventory-domain component. It classifies weapons, armor, consumables, materials, quest items, documents/evidence, valuables and miscellaneous items.
96. Centralized inventory normalization, unique-item protection, stackable item handling, add/remove/remove-all, count/contains and recipe crafting. Existing UI inventory mutations now route through these operations instead of directly changing the list in each callback.
97. Updated the inventory page to group items by category and display stack quantities. Existing item names and the legacy `¦`-delimited persistence format are preserved.
98. Reworked the two existing crafting actions to use atomic recipes. Requirements are checked before any ingredient is removed; a failed recipe or failed output insertion leaves the original inventory untouched.
99. Added `InventoryRulesCheck.kt` for legacy normalization, uniqueness, stackables, categories, successful/failed crafting, exact ingredient consumption, removal semantics and the new-item entry limit. Existing legacy inventories larger than the new-item limit are preserved rather than truncated.
100. Save schema remains version 7. No new save keys, dependencies, Android permissions or resource IDs were introduced.

## Twenty-third-pass verification boundaries

- Recompiled and ran the existing pure Kotlin rules harness; combat, economy, save validation/integrity and 2,000 realm-day differential cases passed.
- Recompiled and ran `QuestRulesCheck.kt`; all assertions passed.
- Compiled `InventoryManager.kt` with `InventoryRulesCheck.kt`; all assertions passed.
- The Android/Compose application was not built. No APK, emulator or device test was run; full app verification remains deferred to the end of Part III.

## Рабочая версия 24 — боевой менеджер и единая модель экипировки
- Добавлен чистый Kotlin `BattleManager`: расчёт атак игрока, входящего урона, последствий поражения и маршрутизация плана награды.
- Боевые кнопки быстрого и тяжёлого удара переведены на единый расчёт `BattleManager.playerAttack`.
- Добавлен `EquipmentManager`: единый приоритет оружия и брони, вычисление бонусов из прежнего списка инвентаря.
- Боевой экран, расчёт защиты и расчёт атак используют общий профиль экипировки.
- Сохранён `SaveManager.CURRENT_SCHEMA = 7`; миграция не требуется.
- Проверки чистых Kotlin-модулей и QuestRulesCheck прошли; Android-сборка не выполнялась.

## Двадцать пятая фундаментальная итерация — EconomyManager и GameState snapshot

101. Добавлен `EconomyManager.kt`; расчёты пайка, потерь от нехватки снабжения, провинциальных доходов и прогноз доходности централизованы в нём. `GameRules` сохранён как фасад совместимости.
102. Закупка провианта, набор армии, обучение гвардии, сбор налогов и улучшение казначейства переведены на неизменяемые транзакции `EconomyState`. Все предусловия проверяются до выдачи нового состояния; отказ не меняет ресурсы.
103. В `MainActivity.kt` реальные callback-действия казначейства и армии используют `EconomyManager`; сообщения и journal entries поступают из результата доменной операции.
104. Добавлен `GameState` как immutable snapshot кампании с отдельным `Realm` snapshot. Суточное обновление королевства теперь обрабатывает snapshot и применяет `RealmDayResult` через `Realm.apply`.
105. Схема сохранений осталась 7: формат SharedPreferences и инвентаря не менялся.

## Границы проверки версии 25

- Pure Kotlin harnesses проверяют `EconomyManager`, `GameState`, `RealmDayProcessor`, `BattleManager`, `EquipmentManager`, `SaveManager`, `QuestManager` и `InventoryManager`.
- Проверка синтаксиса `MainActivity.kt` не эквивалентна полной проверке типов Compose. Полноценная Android-сборка не запускалась из-за отсутствующего Android SDK/Gradle Wrapper в среде.
- APK, эмулятор и физическое устройство не использовались.

## Version 26 — player progression and journal extraction
- Added pure Kotlin PlayerProgressManager with immutable progression state/result and level-up calculation.
- MainActivity XP awards now delegate to PlayerProgressManager while preserving `level * 100` thresholds and one skill point per level.
- Added JournalManager; all direct journal append call sites now route through a single helper.
- Journal retains the newest 500 non-blank entries to bound preference storage growth.
- Save schema remains 7; existing XP keys, journal key, and delimiter are unchanged.
- Validation is recorded in VERSION_26_CHANGELOG.md; no Android build/device test is claimed.

## Двадцать седьмая фундаментальная итерация — CompanionManager

106. Добавлен `CompanionManager.kt`: единый roster спутников, нормализация доверия и чистый расчёт результатов экспедиций.
107. Четыре существующих исхода экспедиций перенесены из Compose callback в менеджер; UI применяет дельты здоровья, золота, влияния, репутации и предметные награды.
108. Сохранены прежние награды, отчёты, ограничение одной экспедиции в день и четырёхчасовая стоимость путешествия. Неизвестное legacy-значение спутника сохраняет прежний fallback на Томаса.
109. Доверие ограничивается диапазоном −100…100 после экспедиции; ключи сохранения и schema 7 не менялись.

## Границы проверки версии 27

- CompanionManager и его regression harness скомпилированы и выполнены; все проверки прошли.
- Существующие pure Kotlin regression harnesses повторно выполнены.
- Android/Compose full build и device tests не выполнялись; проверка синтаксиса MainActivity не заменяет Android compilation.

## Двадцать восьмая фундаментальная итерация — WorldManager и единый маршрут путешествий

110. Добавлен чистый Kotlin `WorldManager.kt` с каталогом 11 известных локаций, каноническим именем порта, базовой длительностью путешествия, сюжетными условиями доступа и признаком опасной дороги.
111. Добавлен типизированный `TravelPlan`: неизвестное место, повторный переход в текущую локацию и закрытая сюжетная локация возвращают отказ; успешный план содержит нормализованное место и часы пути.
112. В `MainActivity.kt` добавлена единая функция `travelToLocation`. Иллюстрированная карта и список мест на странице мира теперь используют общий менеджер, продвигают игровое время, формируют отчёт и записывают переход в журнал.
113. Исправлено расхождение: кнопки `MapPlace` раньше меняли локацию напрямую, минуя время путешествия. Теперь они также используют WorldManager. Существующие значения времени для узлов иллюстрированной карты сохранены.
114. Опасные дороги обозначаются в плане/отчёте, но случайные встречи не добавлялись. Схема сохранения остаётся 7, новые ключи не создавались.

## Проверки версии 28

- Все семь pure Kotlin regression harnesses скомпилированы в одном наборе и выполнены успешно: базовые правила/сохранения/2 000 сравнительных случаев, экономика, инвентарь, задания, прогресс/журнал, спутники и мир.
- Проверки WorldManager охватили 11 локаций, старый alias порта, сюжетные блокировки, неизвестные места, повторный переход, длительность пути и флаг опасной дороги.
- Полная Android/Compose-сборка, APK, эмулятор и устройство не запускались; чистые Kotlin-тесты не подтверждают корректность всех Android-зависимостей.

## Двадцать девятая фундаментальная итерация — TravelManager и дорожная сеть

115. Добавлен чистый Kotlin `TravelManager.kt` с 15 двусторонними дорожными участками между 11 каноническими локациями. Для каждого участка заданы игровые часы и признак опасности.
116. Реализован поиск кратчайшего маршрута по времени. Маршрут содержит промежуточные локации, суммарную длительность и число опасных участков.
117. Интеграция в `MainActivity.travelToLocation`: `WorldManager` по-прежнему проверяет сюжетную доступность назначения, `TravelManager` рассчитывает фактический маршрут, после чего UI продвигает мировое время на сумму часов маршрута и пишет полный путь в отчёт и журнал.
118. Значения часов от старого callback карты больше не определяют продолжительность путешествия: длительность теперь задаёт дорожная сеть. Сигнатура callback оставлена совместимой с текущим UI. Сохранения не менялись, schema остаётся 7.

## Проверки версии 29

- Все восемь pure Kotlin harnesses скомпилированы вместе и выполнены успешно.
- `TravelRulesCheck` проверяет все пары локаций против независимой реализации Floyd–Warshall, обратную достижимость, сумму времени по реальным рёбрам и подсчёт опасных участков.
- Повторно прошли тесты боевых правил, экономики, сохранений, инвентаря, заданий, прогресса, спутников и WorldManager; набор базовых правил содержит 2 000 детерминированных differential cases.
- Android/Compose build, APK, эмулятор и устройство не запускались. Полная интеграционная проверка остаётся на конец части III.

## Версия 30 — дорожные встречи
- Добавлен `RandomEncounterManager` с детерминированным выбором событий по маршруту, времени отправления и счётчику встреч.
- Опасные участки повышают шанс события; ночное отправление добавляет риск; безопасные маршруты в текущем наборе правил не порождают встречи.
- Интегрированы торговец (золото и дорожные припасы), путник (сюжетная запись) и засада (переход в существующую боевую систему).
- Новые ключи сохранения не добавлялись; схема остаётся 7.
- Все девять наборов чистых Kotlin-проверок успешно выполнены. Android-сборка не выполнялась.

## Версия 31 — расписания NPC и обзор фракционных отношений
- Добавлен `NpcScheduleManager.kt`; четыре существовавших расписания персонажей перенесены из локального UI `when` в типизированные интервалы времени. Ночные интервалы корректно пересекают полночь; UI показывает доступных NPC и текущую активность.
- Добавлен `FactionManager.kt`; существующие репутации Короны, торговых гильдий, Народа и Чёрной розы отображаются как нормализованные значения и ранги. Добавлены безопасные функции нормализации и применения дельты, но пока нет новых сюжетных последствий.
- Новые ключи не добавлялись; схема сохранений остаётся 7.
- Чистые Kotlin-модули и десять regression harnesses прошли. Полная Android/Compose-сборка и устройство не тестировались.

## Версия 32 — WorldEventsManager и ежедневная симуляция новостей

120. Добавлен `WorldEventsManager.kt`: детерминированный генератор ежедневных событий королевства, основанный на дне кампании, репутациях, казне и состоянии провинций.
121. Поддерживаются торговый караван, королевский дозор, ярмарка, слухи о Чёрной розе, обострение провинциального кризиса и спокойный день. События возвращают типизированные эффекты, а не изменяют Compose state напрямую.
122. `MainActivity.advanceWorldTime` применяет событие для каждого пересечённого дня в том же цикле, где обрабатываются содержание армии и экономика провинций. Репутационные эффекты проходят через `FactionManager.applyDelta`; остальные значения ограничиваются перед применением.
123. События записываются в существующий журнал и отражаются на карточке текущего события мира. Отдельные ключи сохранения не добавлялись; schema остаётся 7, а однократность обработки обеспечивается прежним `lastUpkeepDay`.

## Проверки версии 32

- Все 12 чистых Kotlin regression harnesses скомпилированы совместно и выполнены успешно, включая 2 000 differential cases игровых дней.
- `WorldEventsRulesCheck` проверил детерминизм, кризисную провинцию, пустые списки и граничные целочисленные входы.
- Статические проверки v31/v32 и разбор AndroidManifest.xml прошли.
- Полная Android/Compose-сборка, APK и тестирование на устройстве не проводились.

## Версия 33 — расписания NPC 2.0 и перемещение знакомых
124. `NpcScheduleManager` переведён с единственного фиксированного места на персонажа на несколько временных сегментов в течение суток. Гаррет перемещается между замком и Северной заставой, Селеста — между Зимним садом и замком, Мира — между Нижним городом и Рыночной площадью, Томас — между портом и Нижним городом.
125. Каждый сегмент задаёт место, деятельность и возможность поговорить. Физическое присутствие отделено от доступности: NPC может быть на месте, но занят, спать или не принимать посетителей.
126. Добавлены `roster(hour)` и `hoursUntilNextChange(hour)`. Страница «Жизнь Эстервала» отображает местонахождение всех четырёх знакомых, статус доступности и время до следующего изменения распорядка.
127. Позиции NPC вычисляются из текущего игрового часа; новые preference keys и поля сохранения не добавлялись. Schema остаётся 7.

## Проверки версии 33
- Все 12 чистых Kotlin regression harnesses скомпилированы совместно и выполнены успешно, включая 2 000 differential cases базовых игровых правил.
- Новый тест расписаний проверяет для каждого часа и каждого NPC ровно одну позицию, корректность переходов, доступность для разговора, алиасы локаций и переход через полночь.
- Статические проверки v31/v32/v33 и XML-разбор AndroidManifest.xml прошли.
- Полная Android/Compose-сборка, APK и тестирование на устройстве не проводились.

## Версия 34 — расписание NPC управляет личными беседами
128. Добавлен `NpcInteractionManager.kt` с типизированными результатами `Available`, `Away` и `Busy`. Он использует текущее расписание NPC и канонические локации мира, поэтому статус разговора не хранится отдельно и не рассинхронизируется с игровым временем.
129. Страница личных бесед теперь показывает, где находится выбранный персонаж и почему разговор недоступен. Темы разговора выключены, если NPC находится в другой локации либо занят; обработчик дополнительно повторяет проверку перед изменением доверия и записью журнала.
130. Для пока не расписанного сюжетного персонажа (герцог Рейнар) временно используется castle-only fallback. Это ограничение явно отмечено в документации и должно быть заменено индивидуальным расписанием в последующем проходе.
131. Новые preference keys и поля сохранения не добавлялись; schema остаётся 7, существующие per-NPC dialog stage/trust данные сохранены.

## Проверки версии 34
- 13 чистых Kotlin regression harnesses скомпилированы совместно и выполнены; базовый `RulesCheck` сохранил 2 000 differential cases.
- `NpcInteractionRulesCheck` проверил физическое присутствие, доступность, занятость, переходы расписаний, legacy alias порта и fallback Рейнара.
- Статические проверки v31/v32/v33/v34 и XML-разбор AndroidManifest.xml прошли.
- Полная Android/Compose-компиляция, APK, эмулятор и физическое устройство не запускались.

## Version 35 — companion relationship separation

135. Replaced the single shared companion expedition bond with four independent bounded bond values for Garrett, Celeste, Mira and Thomas. Selecting another companion no longer carries over the previous companion's relationship score.
136. Added character-specific personal moments with three approaches (support, curiosity about the past, humor), distinct preferred responses, bounded relationship effects and a once-per-campaign-day guard. Personal conversations also require the companion to be physically present and free under the existing NPC schedule; results are added to the existing journal.
137. Added schema 7 → 8 migration. The legacy `companion_bond` value is assigned to the companion selected in the old save; other missing bonds default to 0. Existing new-format values are preserved. The old key remains as a compatibility mirror for the selected companion.
138. Corrected the northern-clan story reward so Mira, who participates in that scene, receives the bond increase even if another companion is selected.
139. Added tests for personal-moment outcomes, bond boundaries, legacy migration and schema-7 manual-save migration. All 13 pure Kotlin test entry points and V30–V35 static integration checks passed. No Android/Compose build or device test was performed.

## Version 36 — политическая повестка фракций
140. Добавлен `FactionAgendaManager.kt`: он вычисляет политические ожидания и конфликты из четырёх уже существующих репутаций, не создавая параллельного состояния сохранения.
141. Повестка может показывать требования двора к порядку, ожидания торговых гильдий по безопасности путей, недовольство горожан, требование Чёрной розы к секретности, конфликт между двором и орденом, а также разрыв между интересами купцов и доверием народа.
142. Каждая повестка содержит источник, давление, возможную выгоду/цену и уровень срочности. Страница «Жизнь Эстервала» показывает до трёх наиболее срочных вопросов.
143. Добавлены регрессионные проверки равновесия, конфликтов, порогов, порядка срочности и экстремальных входов. Схема сохранения остаётся 8: новые поля не нужны.

## Проверки версии 36
- Все 13 чистых Kotlin regression harnesses скомпилированы совместно и прошли, включая 2 000 differential cases обработки игровых дней.
- Статические проверки V30–V36 и XML-разбор AndroidManifest.xml прошли.
- Полная Android/Compose-сборка, APK, эмулятор и физическое устройство не запускались.

## Version 38 — Companion relationships 1.0

Added six persistent companion-to-companion affinity tracks and six tailored disagreement scenes. The player can mediate or openly support one side; the result affects shared pair affinity, is recorded in the journal, and is limited to one resolution per campaign day. Both characters must be physically available at the current location under the NPC timetable. Save schema 10 adds the six affinities, daily marker and report while preserving earlier campaign data. See `VERSION_38_CHANGELOG.md`.

## Version 39 — совместные истории спутников
144. Добавлен `CompanionTeamEventManager.kt` с шестью уникальными совместными сценами — по одной для каждой пары спутников. Для открытия нужны отношения внутри пары не ниже 10 и личное доверие каждого участника к герою не ниже 5.
145. В каждой сцене есть два подхода: действовать сообща или выбрать более осторожный компромисс. Первый вариант повышает парные отношения на 6, второй — на 2; оба повышают личное доверие каждого участника на 2. Все значения ограничены существующими правилами отношений.
146. Страница спутников показывает следующую незавершённую совместную историю. Оба персонажа должны физически находиться рядом и быть свободны по расписанию NPC; проверка повторяется непосредственно при выборе результата.
147. Прогресс шести событий хранится компактной шестибитной маской. Одно событие можно завершить только один раз, а за игровой день разрешено завершить не более одного события. Решения записываются в журнал.
148. Схема сохранений повышена с 10 до 11. Добавлены маска завершения, маркер дня и текст последнего результата. Миграция схем 1–10 сохраняет прежние личные отношения, главы историй и отношения между парами, добавляя только нейтральное состояние новых событий.

## Проверки версии 39
- 15 чистых Kotlin regression harnesses скомпилированы совместно и выполнены; базовый `RulesCheck` включает 2 000 differential cases игровых дней.
- Статические проверки V30–V39 и XML-разбор AndroidManifest.xml прошли.
- Лексическая проверка скобок, строк и комментариев `MainActivity.kt` прошла; все 113 литеральных ключей сохранения зарегистрированы в `SaveManager` либо относятся к динамическим ключам диалогов.
- `SaveSlotStore` и связанные правила сохранений прошли type-check с временными Android/JSON заглушками; это не Android SDK build.
- Полная Android/Compose-компиляция, APK, эмулятор и физическое устройство не запускались.

## Version 41 — crisis consequences

Added transition-based political consequences for provincial unrest, escalation and revolt. Reputation changes are applied through bounded faction rules; escalation/revolt can reduce loyalty in adjacent provinces. Effects are transition-triggered to avoid repeated daily penalties while a province remains in the same stage. All consequences are journaled and included in the daily report. No save-schema change (still schema 11).

- V42: added live cross-system royal-council priorities combining treasury, army supply, provincial stability and faction tension; derived-only, no save-schema change.


## Version 43 — narrative action boundary
Moved deterministic narrative choice validation and resource/reputation calculation into `GameActionRules`. `MainActivity` now applies the immutable result and retains UI, inventory, journal, and save responsibilities. Save schema unchanged at 11. This is an incremental extraction, not a full UI architecture rewrite.

## Version 44 — world clock extraction

Extracted hour/day normalization, day-rollover arithmetic, pending-upkeep calculation, time-of-day phase selection, and transition messaging into the pure Kotlin `WorldClockManager`. `MainActivity.advanceWorldTime` now consumes the immutable clock plan and runs exactly the planned number of daily settlements in the existing order. Intermediate arithmetic uses `Long` to guard against integer overflow. Save schema remains 11. Dedicated tests cover negative/zero/normal/multi-day advances, overdue upkeep, sunrise/dusk, invalid clock inputs, and overflow boundaries.

## Version 46 — аудит границ сохранения
- Сквозная проверка выявила расхождение в обработке четырёхбитной маски расследований: обычная загрузка ограничивала её через `and 0b1111`, а валидатор ручных слотов сохранял произвольное целое значение.
- `SaveManager` теперь ограничивает `world_investigation_flags` диапазоном 0..15; тесты проверяют положительное переполнение и отрицательное значение.
- Схема сохранений остаётся 11. Это исправление нормализации существующего поля, не новая механика.

## Version 47 — save-boundary normalization

Extended manual-save normalization to legacy companion bonds, the three-stage social-conflict counter, companion memory counters, random-event count, crafting count and encounter count. Regression coverage exercises both positive and negative out-of-range values. Save schema remains 11; no migration is required.

## Version 50 — combat state integrity
- Restored combat state is normalized before entering Compose: blank enemy names, non-positive HP, and partial encounters are discarded; valid names and HP are bounded.
- Delayed/duplicate attack callbacks against an inactive encounter are harmless no-ops.
- Defeat wound-day arithmetic now saturates safely at integer boundaries.
- Added a dedicated combat integrity harness. 23 pure Kotlin test entry points and static checks V30–V50 passed; Android build/device validation remains pending. Save schema remains 11.


### Version 51 — progression and choice arithmetic integrity

- Fixed narrative-choice cost overflow for `Int.MIN_VALUE`; affordability checks now use `Long`.
- Made every choice delta overflow-safe and aligned live gold with the persisted 1,000,000 cap.
- Aligned player level, experience, and skill-point normalization with `SaveManager` limits and enforced level/point caps during XP grants.
- Added extreme-value regressions to `GameActionRulesCheck.kt` and new `ProgressionIntegrityRulesCheck.kt`.
- Save schema remains 11. Pure Kotlin boundary tests passed; Android build/device validation remains pending.

## Version 53 — world location and travel integrity
- Added canonical recovery for missing/obsolete saved locations, preserving the legacy port alias and recovering unknown values to the capital.
- Travel planning now rejects unknown origins instead of returning a route-ready plan that cannot be resolved by the road graph.
- Manual save import and runtime restoration use the same location normalization rule.
- Illustrated map travel-time labels now derive from the actual shortest route from the player's current location.
- Added regression coverage for alias conversion, invalid location recovery, invalid travel origins and save-import normalization.
- Pure Kotlin modules and all 25 rule-test entry points pass; Android/Compose build and physical-device tests remain outstanding.

## V55 — current-schema save completeness

Schema-11 manual saves are now required to contain every fixed key emitted by `MainActivity.save()`. This prevents a partial current-version slot from passing validation and silently resetting omitted progress after `SaveSlotStore` clears active preferences. Legacy schema-10 migration remains supported. See `VERSION_55_CHANGELOG.md` and `VERSION_55_VERIFICATION.md`.

## V56 — current-schema manual-slot checksum enforcement

- Tightened the save-slot integrity boundary: schema 11 and future snapshots must include a valid SHA-256 checksum.
- Kept schema 10 and older no-hash slot compatibility; a supplied legacy checksum is still verified.
- The detached snapshot is parsed and validated before checksum policy is applied, and all checks complete before active preferences are cleared.
- Added checksum-policy regression assertions. Save schema remains 11.
- Verification: 26 pure Kotlin test harnesses passed; static checks V30–V56 passed; SaveSlotStore and pure modules type-checked against temporary Android/JSON API stubs. No full Android build/device test.

## V57 — save-schema integer boundary hardening

A deeper save-validation review found that `Number.toInt()` was applied to an untrusted schema marker before its range was checked. Large values could wrap modulo 2^32; specifically `4_294_967_307L` narrowed to `11`, potentially masquerading as the current schema. `SaveManager.validateSnapshot()` now validates the wide `Long` value before narrowing. A focused regression test covers overflow-wrap, out-of-range, fractional, negative, and valid-current cases. Save schema remains 11; no persisted fields or migration formats changed.
