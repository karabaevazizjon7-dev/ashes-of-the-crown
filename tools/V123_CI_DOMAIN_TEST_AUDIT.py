#!/usr/bin/env python3
"""Static audit for the V123 CI integration of platform-independent domain tests."""
from pathlib import Path
import sys

ROOT = Path(__file__).resolve().parents[1]
workflow_path = ROOT / ".github/workflows/android.yml"
guide_path = ROOT / "PART3_ANDROID_BUILD_GUIDE_RU.md"
test_path = ROOT / "tools/PART3_DOMAIN_TESTS.py"
workflow = workflow_path.read_text(encoding="utf-8")
guide = guide_path.read_text(encoding="utf-8")
checks = [
    ("workflow installs Kotlin compiler 2.0.21", "KOTLIN_VERSION=2.0.21" in workflow and "kotlin-compiler-${KOTLIN_VERSION}.zip" in workflow),
    ("compiler distribution is downloaded with failure handling", "curl --fail --location --retry 3" in workflow),
    ("compiler bin directory is added to PATH", '"$RUNNER_TEMP/kotlin/kotlinc/bin" >> "$GITHUB_PATH"' in workflow),
    ("domain test runner executes in CI", "python tools/PART3_DOMAIN_TESTS.py" in workflow),
    ("domain test step precedes Android compile", workflow.find("python tools/PART3_DOMAIN_TESTS.py") < workflow.find(":app:compileDebugKotlin")),
    ("test runner exists", test_path.is_file()),
    ("guide documents the domain test runner", "python tools/PART3_DOMAIN_TESTS.py" in guide),
    ("CI does not package APK", all(x not in workflow for x in ("assembleDebug", "assembleRelease", "app-debug.apk", "app-release.apk"))),
]
failed = []
for label, ok in checks:
    print(("PASS" if ok else "FAIL") + ": " + label)
    if not ok:
        failed.append(label)
print(f"V123_CI_DOMAIN_TEST_AUDIT: {len(checks)-len(failed)} PASS / {len(failed)} FAIL / {len(checks)} total")
sys.exit(1 if failed else 0)
