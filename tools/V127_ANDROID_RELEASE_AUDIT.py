#!/usr/bin/env python3
"""Deep pre-build audit for Ashes of the Crown Part III V127.

This validates project structure and source/resource references. It does not
replace Android Gradle compilation, lint, emulator testing, or device testing.
"""
from pathlib import Path
import os
import re
import shutil
import sys
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
APP = ROOT / "app"
RES = APP / "src/main/res"
JAVA = APP / "src/main/java/com/ashes/crown"
checks = []

def check(name, ok, detail=""):
    checks.append((name, bool(ok), detail))

def read(path):
    return path.read_text(encoding="utf-8", errors="replace") if path.exists() else ""

# 1. Android/Gradle project skeleton and pinned toolchain.
settings = read(ROOT / "settings.gradle.kts")
root_gradle = read(ROOT / "build.gradle.kts")
app_gradle = read(APP / "build.gradle.kts")
check("Root settings include the app module", 'include(":app")' in settings)
check("AGP 8.7.3 is pinned", 'com.android.application") version "8.7.3"' in root_gradle)
check("Kotlin Android and Compose plugins are pinned to 2.0.21", 'org.jetbrains.kotlin.android") version "2.0.21"' in root_gradle and 'org.jetbrains.kotlin.plugin.compose") version "2.0.21"' in root_gradle)
check("Android SDK levels are explicit", all(x in app_gradle for x in ("compileSdk = 35", "minSdk = 26", "targetSdk = 35")))
check("Java and Kotlin bytecode target 17", "JavaVersion.VERSION_17" in app_gradle and 'jvmTarget = "17"' in app_gradle)
check("Compose build feature enabled", re.search(r"buildFeatures\s*\{\s*compose\s*=\s*true", app_gradle) is not None)
check("Application entry point exists", (JAVA / "MainActivity.kt").is_file())
check("Android manifest exists", (APP / "src/main/AndroidManifest.xml").is_file())

# 2. Manifest and values XML parse.
xml_files = sorted([p for p in (APP / "src/main").rglob("*.xml") if p.is_file()])
xml_errors = []
for path in xml_files:
    try:
        ET.parse(path)
    except ET.ParseError as exc:
        xml_errors.append(f"{path.relative_to(ROOT)}: {exc}")
check("All Android XML resources parse", not xml_errors, "; ".join(xml_errors[:5]) or f"{len(xml_files)} XML files")
manifest_text = read(APP / "src/main/AndroidManifest.xml")
check("Launcher activity is exported and declared", 'android:name=".MainActivity"' in manifest_text and 'android:exported="true"' in manifest_text and 'android.intent.action.MAIN' in manifest_text and 'android.intent.category.LAUNCHER' in manifest_text)

# 3. Every explicit resource reference in Kotlin/XML must resolve.
resource_dirs = [p for p in RES.iterdir() if p.is_dir()] if RES.exists() else []
resource_names = {}
for directory in resource_dirs:
    typ = directory.name.split("-")[0]
    if typ in {"drawable", "mipmap", "raw", "layout", "menu", "anim", "animator", "color", "font", "xml", "navigation"}:
        for file in directory.iterdir():
            if file.is_file():
                resource_names.setdefault(typ, set()).add(file.stem)
values_names = set(re.findall(r"<(?:style|string|color|dimen|bool|integer|plurals|array)\s+name=\"([A-Za-z0-9_]+)\"", "\n".join(read(p) for p in RES.glob("values*/*.xml"))))
source_files = sorted(JAVA.rglob("*.kt"))
all_source = "\n".join(read(p) for p in source_files)
missing_refs = []
for typ, name in re.findall(r"R\.(drawable|mipmap|raw|layout|menu|color|font|xml)\.([A-Za-z0-9_]+)", all_source):
    if name not in resource_names.get(typ, set()):
        missing_refs.append(f"R.{typ}.{name}")
for typ, name in re.findall(r"@(?:\+)?(drawable|mipmap|raw|layout|menu|color|font|xml)/([A-Za-z0-9_]+)", "\n".join(read(p) for p in (APP / "src/main").rglob("*.xml"))):
    if name not in resource_names.get(typ, set()):
        missing_refs.append(f"@{typ}/{name}")
check("Kotlin/XML resource references resolve", not missing_refs, "; ".join(sorted(set(missing_refs))[:12]) or "all explicit references resolved")

# 4. Resource integrity: decode images when Pillow is available; validate OGG headers.
images = [p for p in RES.rglob("*") if p.is_file() and p.suffix.lower() in {".jpg", ".jpeg", ".png", ".webp"}]
image_failures = []
try:
    from PIL import Image
    for path in images:
        try:
            with Image.open(path) as im:
                im.verify()
        except Exception as exc:
            image_failures.append(f"{path.name}: {exc}")
    image_detail = f"{len(images)} image files decoded/verified with Pillow"
except ImportError:
    image_detail = f"{len(images)} image files present; Pillow unavailable, decode not checked"
check("Image resources are structurally valid", not image_failures, "; ".join(image_failures[:8]) or image_detail)

music = sorted(RES.rglob("*.ogg"))
ogg_bad = [p.name for p in music if p.stat().st_size < 64 or p.read_bytes()[:4] != b"OggS"]
check("Bundled OGG resources have valid container signatures", len(music) == 7 and not ogg_bad, f"{len(music)} files; invalid: {ogg_bad}")

# 5. Source inventory, save schema, release workflow policy.
check("Kotlin application source inventory is present", len(source_files) >= 30, f"{len(source_files)} Kotlin source files")
save_schema_found = any(re.search(r"CURRENT_SCHEMA\s*=\s*11", read(p)) for p in source_files)
check("Current save schema remains 11", save_schema_found)
workflow = read(ROOT / ".github/workflows/android.yml")
apk_workflow = read(ROOT / ".github/workflows/android-apk-manual.yml")
check("GitHub Actions provisions JDK 17, SDK 35 and Gradle 8.9", "java-version: '17'" in workflow and "platforms;android-35" in workflow and "gradle-version: '8.9'" in workflow)
check("GitHub Actions compiles Kotlin and runs Android lint", ":app:compileDebugKotlin" in workflow and ":app:lintDebug" in workflow)
check("Continuous verification workflow never packages APK", all(token not in workflow for token in ("assembleDebug", "assembleRelease", "bundleRelease", "app-debug.apk", "app-release.apk")))
check("APK workflow is manual and requires explicit YES", "workflow_dispatch:" in apk_workflow and "confirm_build == 'YES'" in apk_workflow and "refs/heads/main" in apk_workflow)
check("Manual APK workflow runs compile and lint before assembleDebug", apk_workflow.find(":app:compileDebugKotlin :app:lintDebug") < apk_workflow.find(":app:assembleDebug"))
check("Manual APK workflow uploads the debug artifact", "app/build/outputs/apk/debug/app-debug.apk" in apk_workflow and "actions/upload-artifact@v4" in apk_workflow)

# 6. Environment readiness is reported separately from source validity.
check("Java is available in current environment", shutil.which("java") is not None, shutil.which("java") or "not found")
gradle_here = shutil.which("gradle") is not None
check("Gradle is available locally or provisioned by GitHub Actions", gradle_here or "gradle-version: '8.9'" in workflow, shutil.which("gradle") or "not installed in this workspace; GitHub Actions provisions Gradle 8.9")
sdk = os.environ.get("ANDROID_HOME") or os.environ.get("ANDROID_SDK_ROOT")
check("Android SDK is configured locally or provisioned by GitHub Actions", bool(sdk) or "platforms;android-35" in workflow, sdk or "not configured in this workspace; GitHub Actions provisions SDK 35")

failed = [item for item in checks if not item[1]]
for name, ok, detail in checks:
    print(("PASS" if ok else "FAIL") + f": {name}" + (f" — {detail}" if detail else ""))
print(f"V127_ANDROID_RELEASE_AUDIT: {len(checks)-len(failed)} PASS / {len(failed)} FAIL / {len(checks)} total")
# Environment availability checks are informative; source/config/resource failures fail the audit.
sys.exit(1 if failed else 0)
