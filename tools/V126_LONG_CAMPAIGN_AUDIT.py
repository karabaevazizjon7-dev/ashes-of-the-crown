#!/usr/bin/env python3
"""Static guard for V126 long-campaign endurance regression coverage."""
from pathlib import Path
ROOT = Path(__file__).resolve().parents[1]
test = (ROOT / "tools/LongCampaignStabilityRulesCheck.kt").read_text(encoding="utf-8")
checks = {
    "180 daily progression iterations": "repeat(180)" in test,
    "world clock rollover exercised": "WorldClockManager.advance(day, hour, 16, upkeepDay)" in test,
    "daily realm settlement exercised": "RealmDayProcessor.processDay" in test,
    "province and resource bounds asserted": all(x in test for x in ("army in 0..100", "provisions in 0..100", "loyalty in 0..100", "ResourceRules.MAX_GOLD")),
    "roads checked in both directions": "TravelManager.findRoute(road.from, road.to)" in test and "TravelManager.findRoute(road.to, road.from)" in test,
    "extreme clock arithmetic checked": "Int.MAX_VALUE" in test,
    "test reports its coverage": "LongCampaignStabilityRulesCheck:" in test,
}
for label, passed in checks.items():
    print(("PASS" if passed else "FAIL") + " — " + label)
print(f"V126_LONG_CAMPAIGN_AUDIT: {sum(checks.values())}/{len(checks)} PASS")
raise SystemExit(0 if all(checks.values()) else 1)
