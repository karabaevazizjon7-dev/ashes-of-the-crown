from pathlib import Path
root = Path(__file__).resolve().parents[1]
manager = (root / 'app/src/main/java/com/ashes/crown/BattleManager.kt').read_text(encoding='utf-8')
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text(encoding='utf-8')
test = (root / 'tools/BattleIntegrityRulesCheck.kt').read_text(encoding='utf-8')
save = (root / 'app/src/main/java/com/ashes/crown/SaveManager.kt').read_text(encoding='utf-8')
checks = {
    'restored battle state is normalized before Compose state initialization': 'BattleManager.restoreEncounter(' in main and 'restoredBattle.enemyName' in main and 'restoredBattle.enemyHealth' in main,
    'partial or dead encounters restore as inactive': 'name.isBlank() || health <= 0' in manager,
    'restored enemy health is bounded': 'savedHealth.coerceIn(0, 100_000)' in manager,
    'late attack callbacks cannot hit an inactive encounter': 'if (enemyHealth <= 0) return AttackResult(0, 0, false)' in manager,
    'defeat wound day uses overflow-safe arithmetic': 'worldDay.toLong().coerceAtLeast(1L)' in manager and 'Int.MAX_VALUE.toLong()' in manager,
    'combat integrity boundary tests exist': all(x in test for x in ('missingName', 'deadEncounter', 'oversizedHealth', 'staleAttack', 'overflowDay')),
    'save schema remains unchanged': 'CURRENT_SCHEMA = 11' in save,
}
for label, ok in checks.items():
    print(('PASS' if ok else 'FAIL') + ': ' + label)
assert all(checks.values())
