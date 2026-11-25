from pathlib import Path
root = Path(__file__).resolve().parents[1]
action = (root / 'app/src/main/java/com/ashes/crown/GameActionRules.kt').read_text(encoding='utf-8')
progress = (root / 'app/src/main/java/com/ashes/crown/PlayerProgressManager.kt').read_text(encoding='utf-8')
save = (root / 'app/src/main/java/com/ashes/crown/SaveManager.kt').read_text(encoding='utf-8')
action_test = (root / 'tools/GameActionRulesCheck.kt').read_text(encoding='utf-8')
progress_test = (root / 'tools/ProgressionIntegrityRulesCheck.kt').read_text(encoding='utf-8')
checks = {
    'choice price uses Long before negation': 'val requiredGold = -choice.gold.toLong()' in action,
    'choice deltas use overflow-safe Long addition': 'value.toLong() + delta.toLong()' in action,
    'choice gold is capped to persisted maximum': 'private const val MAX_GOLD = ResourceRules.MAX_GOLD' in action and '0, MAX_GOLD' in action,
    'reputation and influence saturate rather than wrap': 'addBounded(state.reputation, choice.reputation, -100, 100)' in action and 'addBounded(state.influence, choice.influence, 0, ResourceRules.MAX_INFLUENCE)' in action,
    'live level limit matches save validator': 'const val MAX_LEVEL = 1_000' in progress and '"hero_level" to (1..1_000)' in save,
    'live experience limit matches save validator': 'const val MAX_EXPERIENCE = 1_000_000' in progress and '"experience" to (0..1_000_000)' in save,
    'live skill point limit matches save validator': 'const val MAX_SKILL_POINTS = 100_000' in progress and '"skill_points" to (0..100_000)' in save,
    'progression has explicit level cap': 'while (level < MAX_LEVEL)' in progress,
    'choice overflow and Int.MIN_VALUE price regressions covered': 'impossibleMinCost' in action_test and 'overflowResult' in action_test,
    'progression normalization and cap tests exist': all(x in progress_test for x in ('Int.MAX_VALUE', 'levelCap', 'xpCap', 'cappedPoints')),
    'save schema remains unchanged': 'CURRENT_SCHEMA = 11' in save,
}
for label, ok in checks.items():
    print(('PASS' if ok else 'FAIL') + ': ' + label)
assert all(checks.values())
