from pathlib import Path
root = Path(__file__).resolve().parents[1]
save = (root / 'app/src/main/java/com/ashes/crown/SaveManager.kt').read_text(encoding='utf-8')
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text(encoding='utf-8')
tests = (root / 'tools/RulesCheck.kt').read_text(encoding='utf-8')
checks = {
    'investigation bitmask is registered as an integer save field': '"frontier_stage", "world_investigation_flags", "frontier_route"' in save,
    'save validation bounds the four investigation bits': '"world_investigation_flags" to (0..15)' in save,
    'runtime load also masks unknown investigation bits': 'prefs.getInt("world_investigation_flags", 0) and 0b1111' in main,
    'save regression covers excessive and negative investigation masks': '"world_investigation_flags" to 31' in tests and '"world_investigation_flags" to -7' in tests,
    'save schema remains unchanged': 'CURRENT_SCHEMA = 11' in save,
    'no migration was introduced for this normalization-only fix': 'if (schema < 12)' not in save,
}
for label, ok in checks.items():
    print(('PASS' if ok else 'FAIL') + ': ' + label)
assert all(checks.values())
