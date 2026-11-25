from pathlib import Path

root = Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text(encoding='utf-8')
checks = {
    'story portrait reduced for compact layout': 'StoryLayoutRules.portraitWidthDp(ultraCompact, compactLandscape)' in main and 'StoryLayoutRules.portraitHeightDp(ultraCompact, compactLandscape)' in main,
    'adaptive story text sizing': 'val storyBodySize = StoryLayoutRules.bodyFontSp(' in main and 'textLength = scene.text.length' in main,
    'handwritten scene heading retained': 'fontFamily = FontFamily.Cursive' in main,
    'story consequences removed from reading page': 'Text("ПОСЛЕДСТВИЯ"' not in main,
    'choice labels use compact typography': 'StoryLayoutRules.choiceTitleFontSp(ultraCompact, compactLandscape).sp' in main,
    'illustrations fill the panel using curated 16:9 assets': 'contentScale = ContentScale.Crop' in main,
    'map and inventory remain full-width': '"world", "realm_map", "items"' in main,
    'people and companion workspaces remain full-width': '"people", "companions", "dialogues"' in main,
}
failed = [name for name, ok in checks.items() if not ok]
for name, ok in checks.items():
    print(('PASS' if ok else 'FAIL') + ' ' + name)
if failed:
    raise SystemExit('V87 static checks failed: ' + ', '.join(failed))
print(f'V87_STATIC_CHECKS: {len(checks)} passed')
