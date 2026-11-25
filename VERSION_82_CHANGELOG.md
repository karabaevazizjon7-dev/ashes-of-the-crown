# Version 82 — navigation and new-campaign reset audit

- Audited every route exposed by the primary navigation rail, the “Разделы” page and the compact shortcut row against the `when (selectedPage)` renderer. All exposed targets have a corresponding renderer; the `settings` alias intentionally maps to `about`.
- Compared all `remember`-backed mutable states in `AshesGame` with `resetNewGame()`. All campaign state is reset. The only intentionally retained values are music preference/player lifecycle, slot-list refresh counter and menu visibility.
- No gameplay changes were needed in this checkpoint. Added `V82_STATIC_CHECKS.py` to keep the route/reset contract from drifting.
- Save schema remains 11.
