from pathlib import Path
root = Path(__file__).resolve().parents[1]
src = root / "app/src/main/java/com/ashes/crown"
tools = root / "tools"
checks = [
    ('GameActionRules.kt', 'reputation = addBounded(state.reputation, choice.reputation, -100, 100)', (src / 'GameActionRules.kt').read_text(encoding="utf-8")),
    ('GameActionRules.kt', 'influence = addBounded(state.influence, choice.influence, 0, ResourceRules.MAX_INFLUENCE)', (src / 'GameActionRules.kt').read_text(encoding="utf-8")),
]
for entry in checks:
    if len(entry) == 2: label, ok = entry
    else: label, needle, content = entry; ok = needle in content
    print(("PASS" if ok else "FAIL") + ": " + label)
    assert ok
