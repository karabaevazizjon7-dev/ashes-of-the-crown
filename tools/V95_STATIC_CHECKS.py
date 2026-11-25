from pathlib import Path
import re
root = Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text()
portrait = (root / 'app/src/main/java/com/ashes/crown/PortraitLayoutRules.kt').read_text()
checks = {
    'responsive relation portrait rules are used': 'PortraitLayoutRules.relationPortrait(maxWidth.value.toInt())' in main,
    'portrait dimensions are not hard-coded in relation card': 'Modifier.width(58.dp).height(68.dp)' not in main[main.index('private fun PortraitRelationCard'):],
    'portrait crop preserves card frame': 'contentScale = ContentScale.Crop' in main[main.index('private fun PortraitRelationCard'):],
    'relationship values are clamped to 0..100': 'value.coerceIn(0, 100)' in main[main.index('private fun PortraitRelationCard'):],
    'pure portrait rules include narrow-width breakpoint': 'availableWidthDp < 280' in portrait,
    'story portrait presets preserved': 'fun storyPortrait' in portrait,
    'no save schema migration introduced': not re.search(r'saveSchemaVersion\\s*=\\s*12', main),
}
for name, ok in checks.items(): print(('PASS' if ok else 'FAIL') + ': ' + name)
assert all(checks.values()), 'V95 static checks failed'
# Every bundled audio asset is present and non-empty.
raw = root / 'app/src/main/res/raw'
audio = sorted(p for p in raw.glob('*') if p.suffix.lower() in {'.ogg','.mp3','.wav','.m4a'})
assert len(audio) >= 7 and all(p.stat().st_size > 0 for p in audio)
print(f'PASS: {len(audio)} bundled audio assets are present and non-empty')
