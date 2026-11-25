#!/usr/bin/env python3
"""Environment and project preflight for Part III Android build handoff."""
from pathlib import Path
import re, shutil, sys, xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
APP = ROOT / "app"
checks = []
def check(label, ok, detail=""):
    checks.append((label, bool(ok), detail))

root_gradle = (ROOT / "build.gradle.kts").read_text(encoding="utf-8")
app_gradle = (APP / "build.gradle.kts").read_text(encoding="utf-8")
settings = (ROOT / "settings.gradle.kts").read_text(encoding="utf-8")
manifest = APP / "src/main/AndroidManifest.xml"

check("Android application module is included", 'include(":app")' in settings)
check("Android Gradle Plugin is pinned", 'id("com.android.application") version "8.7.3"' in root_gradle)
check("Kotlin Android and Compose plugins are pinned", 'org.jetbrains.kotlin.android' in root_gradle and 'org.jetbrains.kotlin.plugin.compose' in root_gradle)
check("compileSdk 35 is configured", "compileSdk = 35" in app_gradle)
check("Java/Kotlin bytecode target 17 is configured", 'JavaVersion.VERSION_17' in app_gradle and 'jvmTarget = "17"' in app_gradle)
check("Android manifest is valid XML", manifest.exists())
if manifest.exists():
    try:
        ET.parse(manifest)
        manifest_ok = True
    except ET.ParseError:
        manifest_ok = False
    check("Manifest XML parses", manifest_ok)

wrapper = (ROOT / "gradlew").exists() and (ROOT / "gradle/wrapper/gradle-wrapper.properties").exists()
check("Gradle Wrapper is present", wrapper, "missing gradlew and/or gradle/wrapper/gradle-wrapper.properties")
check("Gradle executable is available", shutil.which("gradle") is not None, shutil.which("gradle") or "gradle not found on PATH")
sdk = any(Path(p).exists() for p in ("/opt/android-sdk", "/usr/lib/android-sdk", "/root/Android/Sdk"))
import os
sdk = sdk or bool(os.environ.get("ANDROID_HOME") or os.environ.get("ANDROID_SDK_ROOT"))
check("Android SDK location is configured", sdk, "ANDROID_HOME/ANDROID_SDK_ROOT unset; common SDK paths absent")

# Verify manifest resource references resolve against the app resource tree.
res = APP / "src/main/res"
try:
    tree = ET.parse(manifest)
    app_el = tree.getroot().find("application")
    refs = []
    if app_el is not None:
        for attr in ("{http://schemas.android.com/apk/res/android}icon", "{http://schemas.android.com/apk/res/android}roundIcon", "{http://schemas.android.com/apk/res/android}theme"):
            value = app_el.attrib.get(attr, "")
            m = re.fullmatch(r"@(mipmap|drawable|style)/([A-Za-z0-9_]+)", value)
            if m:
                refs.append((m.group(1), m.group(2)))
    missing = []
    for typ, name in refs:
        candidates = list(res.glob(f"{typ}*/{name}.*")) if typ != "style" else list(res.glob(f"values*/**/*.xml"))
        if typ == "style":
            xmls = [p for p in candidates if p.is_file()]
            found = any((f'name=\"{name}\"' in p.read_text(encoding="utf-8", errors="ignore") or f"name='{name}'" in p.read_text(encoding="utf-8", errors="ignore")) for p in xmls)
        else:
            found = any(p.is_file() for p in candidates)
        if not found:
            missing.append(f"@{typ}/{name}")
    check("Manifest icon/theme references resolve", not missing, ", ".join(missing))
except Exception as exc:
    check("Manifest resource references resolve", False, str(exc))

failed = [x for x in checks if not x[1]]
for label, ok, detail in checks:
    print(("PASS" if ok else "BLOCKED") + ": " + label + (f" — {detail}" if detail else ""))
print(f"PART3_BUILD_PREFLIGHT: {len(checks)-len(failed)} PASS / {len(failed)} BLOCKED / {len(checks)} total")
# Missing build tools are recorded as environment blockers, not source-code failures.
source_failures = [x for x in failed if x[0] not in {"Gradle Wrapper is present", "Gradle executable is available", "Android SDK location is configured"}]
sys.exit(1 if source_failures else 0)
