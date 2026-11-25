#!/usr/bin/env python3
"""Final Part III V120 source/configuration audit; does not create an APK."""
from pathlib import Path
import re, sys, xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
APP = ROOT / "app"
checks = []
def check(name, ok, detail=""):
    checks.append((name, bool(ok), detail))

manifest = APP / "src/main/AndroidManifest.xml"
try:
    root = ET.parse(manifest).getroot()
    check("Android manifest is well-formed XML", True)
    check("Application manifest node exists", root.find("application") is not None)
except Exception as exc:
    check("Android manifest is well-formed XML", False, str(exc))

app_gradle = (APP / "build.gradle.kts").read_text(encoding="utf-8")
root_gradle = (ROOT / "build.gradle.kts").read_text(encoding="utf-8")
workflow = (ROOT / ".github/workflows/android.yml").read_text(encoding="utf-8")
check("AGP 8.7.3 is pinned", 'com.android.application") version "8.7.3"' in root_gradle)
check("Kotlin 2.0.21 is pinned", 'org.jetbrains.kotlin.android") version "2.0.21"' in root_gradle)
check("Android SDK 35 and min SDK 26 retained", "compileSdk = 35" in app_gradle and "targetSdk = 35" in app_gradle and "minSdk = 26" in app_gradle)
check("Java and Kotlin target 17 retained", "JavaVersion.VERSION_17" in app_gradle and 'jvmTarget = "17"' in app_gradle)
check("CI compiles Kotlin and runs lint", ":app:compileDebugKotlin" in workflow and ":app:lintDebug" in workflow)
check("CI still defers APK packaging", "assembleDebug" not in workflow and "assembleRelease" not in workflow and "app-debug.apk" not in workflow and "app-release.apk" not in workflow)

music = sorted((APP / "src/main/res").rglob("*.ogg"))
check("Seven bundled OGG music tracks present", len(music) == 7, f"found {len(music)}")
check("All bundled OGG files are non-empty", bool(music) and all(p.stat().st_size > 0 for p in music))

java_root = APP / "src/main/java/com/ashes/crown"
source_files = sorted(java_root.glob("*.kt"))
check("MainActivity source exists", (java_root / "MainActivity.kt").is_file())
check("Game domain source modules exist", len(source_files) >= 30, f"found {len(source_files)} Kotlin files")
check("Save schema remains version 11", any("CURRENT_SCHEMA = 11" in p.read_text(encoding="utf-8", errors="ignore") for p in source_files))

# Ensure every explicit R.drawable reference points to an existing drawable resource.
main = (java_root / "MainActivity.kt").read_text(encoding="utf-8")
refs = sorted(set(re.findall(r"R\.drawable\.([A-Za-z0-9_]+)", main)))
res = APP / "src/main/res"
missing = [name for name in refs if not any((res / d).glob(name + ".*") for d in ("drawable", "drawable-hdpi", "drawable-xhdpi", "drawable-xxhdpi", "drawable-xxxhdpi", "drawable-nodpi"))]
check("MainActivity drawable references resolve", not missing, f"{len(refs)} references; missing: {', '.join(missing[:12])}")

failed = [x for x in checks if not x[1]]
for name, ok, detail in checks:
    print(("PASS" if ok else "FAIL") + ": " + name + (f" — {detail}" if detail else ""))
print(f"V120_RELEASE_READINESS_AUDIT: {len(checks)-len(failed)} PASS / {len(failed)} FAIL / {len(checks)} total")
sys.exit(1 if failed else 0)
