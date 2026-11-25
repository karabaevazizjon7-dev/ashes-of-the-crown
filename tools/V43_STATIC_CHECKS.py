from pathlib import Path
root = Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text(encoding='utf-8')
rules = (root / 'app/src/main/java/com/ashes/crown/GameActionRules.kt').read_text(encoding='utf-8')
checks = {
    'story choice rules extracted into a standalone pure Kotlin component': 'internal object GameActionRules' in rules,
    'choice resolver validates paid branches before mutation': 'state.gold.toLong() < requiredGold' in rules,
    'rejected choice returns original immutable state': 'state = state,' in rules,
    'resource bounds remain enforced centrally': all(x in rules for x in ('addBounded(state.guard, choice.guard, 0, 100)', 'addBounded(state.seleste, choice.seleste, -100, 100)', 'addBounded(state.gold, choice.gold, 0, MAX_GOLD)')),
    'Compose delegates choice resolution to rules component': 'GameActionRules.resolveChoiceWithInventory(' in main,
    'MainActivity no longer duplicates choice cost validation': 'val requiredGold = -choice.gold' not in main,
    'choice application still records journal, rewards and saves': all(x in main for x in ('addJournalEntry("${scenes[sceneIndex].title}: ${choice.title}")', 'inventory.clear(); inventory.addAll(resolution.inventory)', 'chosen = true; selectedPage = "story"; save()')),
}
for label, ok in checks.items(): print(('PASS' if ok else 'FAIL') + ': ' + label)
assert all(checks.values())
