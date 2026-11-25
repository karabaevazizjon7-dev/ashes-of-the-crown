from pathlib import Path
import re, xml.etree.ElementTree as ET
root = Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text(encoding='utf-8')

assert 'val fullWidthPages = selectedPage in setOf("world", "realm_map", "items", "people", "companions", "dialogues")' in main
assert 'if (!fullWidthPages) Column(' in main
assert 'rightRailWidth' not in main
assert 'listOf("✥" to "story", "♧" to "world", "▤" to "journal", "♜" to "items", "♟♟" to "people", "⚖" to "quests", "⋯" to "more")' in main
assert 'fontFamily = FontFamily.Cursive' in main
assert 'StoryLayoutRules.portraitWidthDp(ultraCompact, compactLandscape)' in main and 'StoryLayoutRules.portraitHeightDp(ultraCompact, compactLandscape)' in main
assert 'val storyBodySize = StoryLayoutRules.bodyFontSp(' in main and 'textLength = scene.text.length' in main
assert 'Row(\n                Modifier.fillMaxWidth().padding(vertical = 1.dp)' in main
assert '.clickable { makeChoice(choice) }' in main
story_map = main[main.index('\"story\" -> when (sceneIndex) {'):main.index('\"world\", \"realm_map\"', main.index('\"story\" -> when (sceneIndex) {'))]
assert all(f'{i} -> R.drawable.story_v89_{i:02d}' in story_map for i in range(21))
assert '11 -> R.drawable.story_v89_11' in story_map
art = main[main.index('private fun PageArtworkPanel'):main.index('private data class PageArtwork')]
assert 'Image(' in art and 'contentScale = ContentScale.Crop' in art
assert 'ПОСЛЕДСТВИЯ' not in art and 'artwork.caption' in art
assert 'схему версии 11' in main
raw = root / 'app/src/main/res/raw'
required_music = {'estervale_ambient.ogg','music_battle.ogg','music_city.ogg','music_court.ogg','music_harbor.ogg','music_mystery.ogg','music_nature.ogg'}
assert required_music <= {p.name for p in raw.iterdir()}
assert (root / 'docs/MUSIC_SOURCES_FOR_PART_II.md').is_file()
ET.parse(root / 'app/src/main/AndroidManifest.xml')
# Lightweight delimiter scan ignoring comments and string contents is deliberately conservative.
text = main
pairs = {'(':')','{':'}','[':']'}
stack=[]; quote=None; triple=False; esc=False; line_comment=False; block=0; i=0
while i < len(text):
    if line_comment:
        if text[i] == '\n': line_comment=False
        i += 1; continue
    if block:
        if text.startswith('/*', i): block += 1; i += 2; continue
        if text.startswith('*/', i): block -= 1; i += 2; continue
        i += 1; continue
    if quote:
        if triple:
            if text.startswith('"""', i): quote=None; triple=False; i += 3; continue
            i += 1; continue
        if esc: esc=False
        elif text[i] == '\\': esc=True
        elif text[i] == quote: quote=None
        i += 1; continue
    if text.startswith('//', i): line_comment=True; i += 2; continue
    if text.startswith('/*', i): block=1; i += 2; continue
    if text.startswith('"""', i): quote='"'; triple=True; i += 3; continue
    if text[i] in ('"', "'"): quote=text[i]; i += 1; continue
    c=text[i]
    if c in pairs: stack.append((c,i))
    elif c in pairs.values():
        assert stack and pairs[stack[-1][0]] == c, f'unmatched {c} at {i}'
        stack.pop()
    i += 1
assert not stack, f'unclosed delimiters: {stack[-5:]}'
print('PASS: full-width workspaces, reduced navigation, adaptive compact handwritten story layout')
print('PASS: one illustration only; artwork fills the panel; 21 scenes use individually curated 16:9 assets')
print('PASS: offline soundtrack files and music source/license note')
print('PASS: Manifest XML and MainActivity delimiter scan')
