from pathlib import Path
root = Path(__file__).resolve().parents[1]
save = (root / 'app/src/main/java/com/ashes/crown/SaveManager.kt').read_text(encoding='utf-8')
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text(encoding='utf-8')
test = (root / 'tools/LegacyPreferenceNormalizationRulesCheck.kt').read_text(encoding='utf-8')
checks = {
    'shared integer normalizer reuses manual-save bounds': 'fun normalizeInteger(key: String, value: Int, maxSceneIndex: Int)' in save and 'boundedFields(maxSceneIndex)' in save,
    'gold and health preferences normalized on direct restore': 'normalizeInteger("gold", prefs.getInt("gold", 18)' in main and 'normalizeInteger("hp", prefs.getInt("hp", 100)' in main,
    'economy and faction values normalized on direct restore': all(f'normalizeInteger("{key}",' in main for key in ('rep','guard','seleste','inf','f_crown','f_merchants','f_people','f_rose')),
    'progression and counter values normalized on direct restore': all(f'normalizeInteger("{key}",' in main for key in ('experience','hero_level','skill_points','skill_power','skill_guard','encounters','crafted_count','expedition_runs','battle_wins','battle_defeats')),
    'memories and quest/social flags normalized on direct restore': all(f'normalizeInteger("{key}",' in main for key in ('mem_garrett','mem_seleste','mem_thomas','mem_mira','events','personal_quests','social_event_flags','social_action_flags')),
    'extreme legacy preference tests cover low and high bounds': all(x in test for x in ('Int.MIN_VALUE','Int.MAX_VALUE','"gold"','"hero_level"','"encounters"','"weather_seed"')),
    'save schema remains 11': 'const val CURRENT_SCHEMA = 11' in save,
}
for label, ok in checks.items():
    print(('PASS' if ok else 'FAIL') + ': ' + label)
assert all(checks.values())
