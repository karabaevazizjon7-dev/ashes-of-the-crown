from pathlib import Path
root = Path(__file__).resolve().parents[1]
src = root / "app/src/main/java/com/ashes/crown"
tools = root / "tools"
checks = [
    ('RulesCheck.kt', '2_000', (tools / 'RulesCheck.kt').read_text(encoding="utf-8")),
    ('V57_STATIC_CHECKS.py', 'schema value is widened', (tools / 'V57_STATIC_CHECKS.py').read_text(encoding="utf-8")),
]
for entry in checks:
    if len(entry) == 2: label, ok = entry
    else: label, needle, content = entry; ok = needle in content
    print(("PASS" if ok else "FAIL") + ": " + label)
    assert ok
