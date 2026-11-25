from pathlib import Path
root = Path(__file__).resolve().parents[1]
world = (root / 'app/src/main/java/com/ashes/crown/WorldManager.kt').read_text(encoding='utf-8')
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text(encoding='utf-8')
save = (root / 'app/src/main/java/com/ashes/crown/SaveManager.kt').read_text(encoding='utf-8')
world_test = (root / 'tools/WorldRulesCheck.kt').read_text(encoding='utf-8')
rules_test = (root / 'tools/RulesCheck.kt').read_text(encoding='utf-8')
checks = {
    'unknown origin rejected before travel plan is issued': 'if (location(origin) == null)' in world and 'Текущее местоположение не распознано' in world,
    'persisted location normalized to a canonical map node': 'fun normalizeLocation(name: String?)' in world and 'location(candidate)?.name ?: "Королевский замок"' in world,
    'legacy port alias is normalized': '"Порт Эстервала" -> "Порт Веймар"' in world,
    'MainActivity validates restored location': 'WorldManager.normalizeLocation(prefs.getString("location", "Королевский замок"))' in main,
    'manual save import normalizes location': 'key == "location" && value is String' in save and 'WorldManager.normalizeLocation(value)' in save,
    'regressions cover alias, invalid location and invalid origin': all(x in world_test for x in ('normalizeLocation("Порт Эстервала")', 'normalizeLocation("Несуществующий тракт")', 'corruptOrigin')),
    'manual save tests cover location normalization': all(x in rules_test for x in ('invalidLocationSave', '"location" to "Порт Эстервала"', 'acceptedLegacy.values["location"] == "Порт Веймар"')),
    'map displays route-specific travel duration': 'TravelManager.findRoute(currentLocation, "Крепость Вальден")?.totalHours' in main and 'TravelManager.findRoute(currentLocation, "Порт Веймар")?.totalHours' in main,
    'save schema remains unchanged': 'CURRENT_SCHEMA = 11' in save,
}
for label, ok in checks.items():
    print(('PASS' if ok else 'FAIL') + ': ' + label)
assert all(checks.values())
