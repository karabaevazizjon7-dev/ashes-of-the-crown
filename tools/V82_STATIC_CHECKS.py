from pathlib import Path
import re
root = Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text(encoding='utf-8')
branches = set(re.findall(r'^\s*"([a-z_]+)"\s*->', main, re.M))
nav_match = re.search(r'listOf\((.*?)\)\.forEach \{ \(icon, key\)', main, re.S)
assert nav_match, 'Primary navigation rail not found'
nav_targets = set(re.findall(r'"[^"]+"\s+to\s+"([a-z_]+)"', nav_match.group(1)))
# The primary rail is intentionally compact; secondary renderers remain reachable through the More page.
more_branch = main.find('"more" -> RenderPage_more()')
assert more_branch >= 0, 'More page renderer missing'
normalized_nav = nav_targets
all_targets = normalized_nav | {'story', 'about'}
missing = sorted(all_targets - branches)
assert not missing, f'Navigation targets without page renderer: {missing}'

# Verify campaign-state reset coverage for every remember-backed var; only UI/audio/slot state is exempt.
state_vars = set(re.findall(r'\bvar\s+(\w+)\s+by\s+remember\s*\{', main))
reset_start = main.index('    fun resetNewGame() {')
reset_end = main.index('\n    fun makeChoice', reset_start)
reset = main[reset_start:reset_end]
not_reset = {name for name in state_vars if not re.search(r'\b' + re.escape(name) + r'\s*(?:=|\.clear\s*\()', reset)}
allowed = {'musicEnabled', 'slotRefresh', 'menuVisible', 'musicPlayer'}
assert not (not_reset - allowed), f'Campaign state missing from new-game reset: {sorted(not_reset - allowed)}'
assert not_reset <= allowed
print(f'PASS: {len(all_targets)} navigation targets have page renderers')
print(f'PASS: {len(state_vars)} remember-backed states audited; only {sorted(not_reset)} are intentionally not campaign-reset')
print('V82 static checks: PASS')
