from pathlib import Path
root = Path(__file__).resolve().parents[1]
save = (root / 'app/src/main/java/com/ashes/crown/SaveManager.kt').read_text(encoding='utf-8')
tests = (root / 'tools/RulesCheck.kt').read_text(encoding='utf-8')
checks = {
    'legacy companion bonds are bounded': all(f'"{key}" to (-100..100)' in save for key in ('bond_thomas', 'bond_mira', 'bond_reynar')),
    'social conflict is bounded to its three stages': '"social_conflict" to (0..3)' in save,
    'companion memory counters are bounded': all(f'"{key}" to (0..1_000_000)' in save for key in ('mem_garrett', 'mem_seleste', 'mem_thomas', 'mem_mira')),
    'event, crafting and encounter counters are bounded': all(f'"{key}" to (0..1_000_000)' in save for key in ('events', 'crafted_count', 'encounters')),
    'regression test covers upper and lower overflow': 'val v47Bounds = SaveManager.validateSnapshot' in tests and '"bond_thomas" to 900' in tests and '"bond_mira" to -900' in tests,
    'save schema remains unchanged': 'CURRENT_SCHEMA = 11' in save and 'if (schema < 12)' not in save,
}
for label, ok in checks.items():
    print(('PASS' if ok else 'FAIL') + ': ' + label)
assert all(checks.values())
