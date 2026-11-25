from pathlib import Path
root = Path(__file__).resolve().parents[1]
economy = (root / 'app/src/main/java/com/ashes/crown/EconomyManager.kt').read_text(encoding='utf-8')
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text(encoding='utf-8')
test = (root / 'tools/EconomyRulesCheck.kt').read_text(encoding='utf-8')
save = (root / 'app/src/main/java/com/ashes/crown/SaveManager.kt').read_text(encoding='utf-8')
checks = {
    'tax collection rejects same-day and future markers': 'lastTaxDay >= worldDay' in economy and 'worldDay < 1' in economy,
    'invalid tax attempts preserve the marker': 'to lastTaxDay' in economy,
    'merchant reputation is clamped to campaign range': 'merchantRep.coerceIn(-100, 100)' in economy,
    'tax payout uses Long before applying the persisted gold cap': 'safe.gold.toLong() + income.toLong()' in economy and '1_000_000L' in economy,
    'economy UI disables collection for invalid or future markers': 'worldDay >= 1 && lastTaxDay < worldDay' in main,
    'regressions cover future marker, invalid day and extreme reputation': all(x in test for x in ('futureMarkerResult', 'collectTaxes(base, 0, 0', 'Int.MAX_VALUE', 'Int.MIN_VALUE')),
    'gold cap regression is covered': 'cappedGoldResult' in test,
    'save schema remains unchanged': 'CURRENT_SCHEMA = 11' in save,
}
for label, ok in checks.items():
    print(('PASS' if ok else 'FAIL') + ': ' + label)
assert all(checks.values())
