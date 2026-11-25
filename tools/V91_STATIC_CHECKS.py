from pathlib import Path
root = Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text()
rules = (root / 'app/src/main/java/com/ashes/crown/StoryLayoutRules.kt').read_text()
assert 'Text(choice.title' in main and 'Text(choice.detail' in main
assert 'maxLines = 2, overflow = TextOverflow.Ellipsis' not in main[main.index('fun RenderPage_fallback()'):main.index('private fun PageArtworkPanel')]
assert 'Text(scene.text' in main
assert 'ultraCompact -> 8.0f' in rules
assert 'ultraCompact -> 7.0f' in rules
print('V91_STATIC_CHECKS: PASS (5 checks)')
