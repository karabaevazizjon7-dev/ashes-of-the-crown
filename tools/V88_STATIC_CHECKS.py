from pathlib import Path
import re
import xml.etree.ElementTree as ET
root = Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text(encoding='utf-8')
rules = (root / 'app/src/main/java/com/ashes/crown/StoryLayoutRules.kt').read_text(encoding='utf-8')
checks = {
    'story typography delegated to pure responsive rules': 'StoryLayoutRules.bodyFontSp(' in main,
    'font size considers actual viewport height': 'screenHeightDp = screenHeightDp' in main and 'screenHeightDp: Int' in rules,
    'font size considers narrative length and choices': 'textLength = scene.text.length' in main and 'choiceCount = availableChoices.size' in main,
    'font scale has a readability floor': '.coerceIn(7.0f, 10.8f)' in rules,
    'portrait scales down for short landscape screens': 'portraitWidthDp' in main and 'compactLandscape -> 42' in rules,
    'choice titles can wrap rather than being clipped to one line': 'maxLines = 2, overflow = TextOverflow.Ellipsis)' in main,
    'choice descriptions can wrap rather than being clipped to one line': 'choiceDetailFontSp' in main and 'maxLines = 2, overflow = TextOverflow.Ellipsis)' in main,
    'illustrations fill the panel using curated 16:9 assets': 'contentScale = ContentScale.Crop' in main,
}
failed = [k for k,v in checks.items() if not v]
for name, ok in checks.items(): print(('PASS ' if ok else 'FAIL ') + name)
if failed: raise SystemExit('V88 checks failed: ' + ', '.join(failed))
ET.parse(root / 'app/src/main/AndroidManifest.xml')
print('PASS AndroidManifest XML')
print(f'V88_STATIC_CHECKS: {len(checks)+1} passed')
