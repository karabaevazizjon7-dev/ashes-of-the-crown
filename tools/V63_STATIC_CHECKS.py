from pathlib import Path
root = Path(__file__).resolve().parents[1]
src = root / "app/src/main/java/com/ashes/crown"
tools = root / "tools"
checks = [
    ('ResourceRules.kt', 'const val MAX_GOLD = 1_000_000', (src / 'ResourceRules.kt').read_text(encoding="utf-8")),
    ('MainActivity.kt', 'ResourceRules.addGold(gold, rewardGold)', (src / 'MainActivity.kt').read_text(encoding="utf-8")),
]
for entry in checks:
    if len(entry) == 2: label, ok = entry
    else: label, needle, content = entry; ok = needle in content
    print(("PASS" if ok else "FAIL") + ": " + label)
    assert ok
