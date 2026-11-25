from pathlib import Path
import re
root = Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text()
travel = (root / 'app/src/main/java/com/ashes/crown/TravelManager.kt').read_text()
world = (root / 'app/src/main/java/com/ashes/crown/WorldManager.kt').read_text()
checks = {
    'map travel durations come from the route planner': '?.totalHours ?: ' not in main[main.index('@Composable private fun RealmIllustratedMap'):main.index('@Composable private fun RealmMapNode')],
    'unavailable routes disable travel actions': 'enabled = !active && unlocked && hours != null' in main,
    'unavailable routes are explained to the player': 'Нет доступного пути' in main,
    'map nodes may shrink within their row': 'modifier.widthIn(min = 0.dp)' in main,
    'all map node destinations exist in the world catalogue': all(name in world for name in ['Крепость Вальден','Зимний сад','Северная застава','Воронья деревня','Лесной тракт','Королевский замок','Нижний город','Рыночная площадь','Старая часовня','Порт Веймар']),
    'map route source is the canonical travel manager': 'TravelManager.findRoute(currentLocation' in main and 'fun findRoute(originName: String, destinationName: String)' in travel,
}
for name, ok in checks.items(): print(('PASS' if ok else 'FAIL') + ': ' + name)
assert all(checks.values())
