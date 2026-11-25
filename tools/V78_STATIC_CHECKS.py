from pathlib import Path
root = Path(__file__).resolve().parents[1]
src = root / "app/src/main/java/com/ashes/crown"
tools = root / "tools"
checks = [
    ("manifest source marker", '<manifest', (root / "app/src/main/AndroidManifest.xml").read_text(encoding="utf-8")),
    (".github/workflows directory exists", (root / ".github/workflows").is_dir()),
]
for entry in checks:
    if len(entry) == 2: label, ok = entry
    else: label, needle, content = entry; ok = needle in content
    print(("PASS" if ok else "FAIL") + ": " + label)
    assert ok
