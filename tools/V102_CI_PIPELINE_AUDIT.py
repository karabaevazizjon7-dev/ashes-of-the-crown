#!/usr/bin/env python3
"""Static guardrails for the Part III pre-release CI workflow."""
from pathlib import Path
import sys

ROOT = Path(__file__).resolve().parents[1]
workflow_path = ROOT / ".github/workflows/android.yml"
workflow = workflow_path.read_text(encoding="utf-8")
app_gradle = (ROOT / "app/build.gradle.kts").read_text(encoding="utf-8")
checks = []
def check(name: str, ok: bool, detail: str = ""):
    checks.append((name, bool(ok), detail))

check("CI workflow exists", workflow_path.is_file())
check("CI uses JDK 17", "java-version: '17'" in workflow)
check("CI provisions Android SDK 35", "platforms;android-35" in workflow and "build-tools;35.0.0" in workflow)
check("CI pins compatible Gradle 8.9", "gradle-version: '8.9'" in workflow)
check("CI compiles Kotlin", ":app:compileDebugKotlin" in workflow)
check("CI runs Android lint", ":app:lintDebug" in workflow)
check("CI does not package APK before final gate", "assembleDebug" not in workflow and "assembleRelease" not in workflow)
check("CI does not upload a premature APK", "app-debug.apk" not in workflow and "app-release.apk" not in workflow)
check("Workflow documents deferred APK packaging", "deferred until the final release gate" in workflow)
check("Application SDK settings remain unchanged", "compileSdk = 35" in app_gradle and "targetSdk = 35" in app_gradle and "minSdk = 26" in app_gradle)

for name, ok, detail in checks:
    print(("PASS" if ok else "FAIL") + ": " + name + (f" — {detail}" if detail else ""))
failed = sum(not ok for _, ok, _ in checks)
print(f"V102_CI_PIPELINE_AUDIT: {len(checks)-failed} PASS / {failed} FAIL / {len(checks)} total")
sys.exit(1 if failed else 0)
