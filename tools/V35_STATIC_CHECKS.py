from pathlib import Path
import re
import xml.etree.ElementTree as ET

root = Path(__file__).resolve().parents[1]
main = (root / "app/src/main/java/com/ashes/crown/MainActivity.kt").read_text(encoding="utf-8")
companion = (root / "app/src/main/java/com/ashes/crown/CompanionManager.kt").read_text(encoding="utf-8")
save = (root / "app/src/main/java/com/ashes/crown/SaveManager.kt").read_text(encoding="utf-8")
checks = {
    "each companion has its own persisted relationship value": all(key in main for key in ("bond_companion_garrett", "bond_companion_celeste", "bond_companion_mira", "bond_companion_thomas")),
    "legacy single bond is read as a migration fallback": 'prefs.getInt("companion_bond", 0)' in main,
    "selected companion bond is read through a single accessor": "fun selectedCompanionBond()" in main,
    "frontier story bond reward is assigned to Mira": 'setCompanionBond("Мира", companionBondMira + 2)' in main,
    "personal moment is integrated into the companions page": 'CompanionManager.personalMoment(companion)' in main,
    "personal conversation requires the companion to be physically available": 'NpcInteractionManager.evaluate(companion, currentLocation, worldHour)' in main and 'canHavePersonalMoment' in main,
    "personal conversation click handler rechecks NPC availability": 'NpcInteractionManager.isAvailable(companion, currentLocation, worldHour)' in main,
    "personal moment has three explicit approaches": all(name in main for name in ("CompanionApproach.SUPPORT", "CompanionApproach.ASK_ABOUT_PAST", "CompanionApproach.HUMOR")),
    "personal conversations are limited to once per campaign day": 'companionTalkDay != worldDay' in main and 'putInt("companion_talk_day", companionTalkDay)' in main,
    "conversation results enter the journal": 'Личный разговор с ${moment.companionName}' in main,
    "CompanionManager has a pure decision resolver": "fun resolvePersonalMoment(" in companion,
    "legacy bond migration preserves the selected companion bond": "fun migrateLegacyBonds(" in companion,
    "save schema is advanced to version 8": "CURRENT_SCHEMA = 11" in save,
    "schema 7 migration adds four companion bond fields": all(key in save for key in ("bond_companion_garrett", "bond_companion_celeste", "bond_companion_mira", "bond_companion_thomas")),
    "new report field is registered as a string save value": '"companion_moment_report"' in save,
}
for label, passed in checks.items():
    print(("PASS" if passed else "FAIL") + ": " + label)
assert all(checks.values())

save_body = main[main.index("    fun save() {"):main.index("    val saveSlotStore")]
written_keys = {key for _, key in re.findall(r'\.put(Int|String|Boolean|Long|Float|StringSet)\("([^"]+)"', save_body)}
registered = set()
for group in ("integerKeys", "stringKeys", "booleanKeys"):
    match = re.search(r"private val " + group + r" = setOf\((.*?)\n    \)", save, re.S)
    if match:
        registered.update(re.findall(r'"([^"]+)"', match.group(1)))
unregistered = {key for key in written_keys - registered if not (key.startswith("dialog_stage_") or key.startswith("dialog_trust_"))}
assert not unregistered, f"Unregistered save keys: {sorted(unregistered)}"
print(f"All {len(written_keys)} literal MainActivity save keys are registered or covered by dialogue-key validation")
ET.parse(root / "app/src/main/AndroidManifest.xml")
print("AndroidManifest.xml parsed successfully")
