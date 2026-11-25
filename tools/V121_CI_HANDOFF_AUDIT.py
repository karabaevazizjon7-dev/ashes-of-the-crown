from pathlib import Path
import sys
root = Path(__file__).resolve().parents[1]
w = (root / '.github/workflows/android.yml').read_text(encoding='utf-8')
g = (root / 'PART3_ANDROID_BUILD_GUIDE_RU.md').read_text(encoding='utf-8')
checks = {
    'JDK 17 setup exists': "java-version: '17'" in w,
    'Android SDK 35 setup exists': "platforms;android-35" in w and "build-tools;35.0.0" in w,
    'Gradle 8.9 setup exists': 'gradle-version:' in w and "'8.9'" in w,
    'Kotlin compile task exists': ':app:compileDebugKotlin' in w,
    'Android lint task exists': ':app:lintDebug' in w,
    'Gradle stacktrace enabled': '--stacktrace' in w,
    'Lint report artifact preserved': 'actions/upload-artifact@v4' in w and 'android-lint-report' in w,
    'No APK packaging task': all(x not in w for x in (':app:assemble', ':app:bundle', 'upload-apk')),
    'Russian handoff guide exists': 'Вариант A: GitHub Actions' in g and 'Вариант B: Android Studio' in g,
    'Guide keeps APK deferred': 'APK остаётся отложенным' in g,
}
for name, ok in checks.items(): print(('PASS' if ok else 'FAIL') + ': ' + name)
failed = sum(not x for x in checks.values())
print(f'V121_CI_HANDOFF_AUDIT: {len(checks)-failed}/{len(checks)} PASS')
sys.exit(1 if failed else 0)
