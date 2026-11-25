from pathlib import Path
root = Path(__file__).resolve().parents[1]
src = root / "app/src/main/java/com/ashes/crown"
tools = root / "tools"
checks = [
    ('VERSION_80_FINAL_AUDIT.md', 'V80', (root / 'VERSION_80_FINAL_AUDIT.md').read_text(encoding="utf-8")),
    ('VERSION_58_AUDIT.md', 'V58', (root / 'VERSION_58_AUDIT.md').read_text(encoding="utf-8")),
]
for entry in checks:
    if len(entry) == 2: label, ok = entry
    else: label, needle, content = entry; ok = needle in content
    print(("PASS" if ok else "FAIL") + ": " + label)
    assert ok
