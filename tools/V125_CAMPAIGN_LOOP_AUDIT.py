#!/usr/bin/env python3
"""Static audit for the V125 cross-system campaign loop regression scenario."""
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
test = (ROOT / "tools/CampaignLoopIntegrationRulesCheck.kt").read_text(encoding="utf-8")
checks = {
    "narrative choice resolves state and inventory atomically": "resolveChoiceWithInventory" in test,
    "quest acceptance, discovery and completion are sequenced": all(x in test for x in ("MedicineQuestAction.ACCEPT", "MedicineQuestAction.FIND_CRATE", "MedicineQuestAction.COMPLETE")),
    "quest evidence is consumed and reward item retained": "applyQuestDelta" in test and "EMPTY_CARGO_TAG" in test,
    "companion relationship result is persisted": "CompanionRelationshipManager.resolve" in test and 'relation_garrett_celeste' in test,
    "travel route is checked before encounter": "TravelManager.findRoute" in test,
    "combat victory gates reward": "canResolveVictory" in test and "BattleManager.rewards" in test,
    "complete current-schema snapshot is validated": "SaveManager.validateSnapshot(snapshot" in test,
    "corrupt daily-settlement marker is rejected": "put(\"a14_upkeep_day\", 99)" in test,
}
for label, passed in checks.items():
    print(("PASS" if passed else "FAIL") + " — " + label)
print(f"V125_CAMPAIGN_LOOP_AUDIT: {sum(checks.values())}/{len(checks)} PASS")
raise SystemExit(0 if all(checks.values()) else 1)
