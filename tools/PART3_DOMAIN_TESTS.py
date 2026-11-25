#!/usr/bin/env python3
"""Compile the platform-independent game domain and execute every Kotlin rule check."""
from pathlib import Path
import re, shutil, subprocess, sys, tempfile

ROOT = Path(__file__).resolve().parents[1]
JAVA = shutil.which("java")
KOTLINC = shutil.which("kotlinc")
if not JAVA or not KOTLINC:
    print("BLOCKED: Java and kotlinc are required for the domain test suite")
    sys.exit(2)

sources = sorted((ROOT / "app/src/main/java/com/ashes/crown").glob("*.kt"))
sources = [p for p in sources if p.name not in {"MainActivity.kt", "SaveSlotStore.kt"}]
test_files = sorted((ROOT / "tools").glob("*.kt"))
classes = []
for path in test_files:
    content = path.read_text(encoding="utf-8")
    if "fun main" not in content:
        continue
    match = re.search(r"\bobject\s+(\w+)", content)
    classes.append(match.group(1) if match else path.stem + "Kt")

with tempfile.TemporaryDirectory(prefix="ashes-part3-tests-") as temp:
    jar = Path(temp) / "domain-tests.jar"
    compile_cmd = [KOTLINC, *map(str, sources), *map(str, test_files), "-include-runtime", "-d", str(jar)]
    print(f"Compile platform-independent domain: {len(sources)} source files + {len(test_files)} test files")
    compiled = subprocess.run(compile_cmd, cwd=ROOT, text=True)
    if compiled.returncode:
        sys.exit(compiled.returncode)
    failures = []
    for class_name in classes:
        result = subprocess.run([JAVA, "-cp", str(jar), f"com.ashes.crown.{class_name}"], cwd=ROOT, text=True)
        if result.returncode:
            failures.append(class_name)
    print(f"PART3_DOMAIN_TESTS: {len(classes)-len(failures)} PASS / {len(failures)} FAIL / {len(classes)} suites")
    if failures:
        print("Failed suites: " + ", ".join(failures))
        sys.exit(1)
