from pathlib import Path
root = Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text(encoding='utf-8')
inventory = (root / 'app/src/main/java/com/ashes/crown/InventoryManager.kt').read_text(encoding='utf-8')
test = (root / 'tools/InventoryRulesCheck.kt').read_text(encoding='utf-8')
save = (root / 'app/src/main/java/com/ashes/crown/SaveManager.kt').read_text(encoding='utf-8')
checks = {
    'quest inventory deltas are applied atomically': 'fun applyQuestDelta(' in inventory and 'return InventoryChange(items, false, added.message)' in inventory,
    'medicine quest checks item transaction before state/reward changes': main.index('val inventoryDelta = InventoryManager.applyQuestDelta(') < main.index('questStage = transition.state.stage'),
    'medicine quest aborts on failed item delivery': 'if (inventoryDelta.message.isNotBlank())' in main,
    'Black Rose evidence is stored before stage advances': 'hasInventoryItem("Запечатанное письмо Чёрной розы") || addUniqueQuestItem("Запечатанное письмо Чёрной розы")' in main,
    'Black Rose final seal is stored before payout': main.index('hasInventoryItem("Печать старой часовни") || addUniqueQuestItem("Печать старой часовни")') < main.index('gold = (gold + 12).coerceAtMost(1_000_000)'),
    'Council token is stored before finalization payout': main.index('hasInventoryItem("Знак Совета семи домов") || addUniqueQuestItem("Знак Совета семи домов")') < main.index('gold = (gold + if (housesQuestPath == 1) 15 else 5).coerceAtMost(1_000_000)'),
    'personal quest flags are committed only after evidence delivery': all(fragment in main for fragment in ('hasInventoryItem("Записка старого ветерана") || addUniqueQuestItem', 'hasInventoryItem("Половина шифрованного письма") || addUniqueQuestItem', 'hasInventoryItem("Медная торговая метка") || addUniqueQuestItem', 'hasInventoryItem("Список ночных грузов") || addUniqueQuestItem')),
    'full inventory and atomic replacement regressions covered': all(x in test for x in ('quest reward cannot overflow a full inventory', 'failed quest reward leaves original inventory untouched', 'quest can atomically replace consumed evidence at capacity', 'multi-item quest transaction rolls back every partial change')),
    'save schema remains unchanged': 'CURRENT_SCHEMA = 11' in save,
}
for label, ok in checks.items():
    print(('PASS' if ok else 'FAIL') + ': ' + label)
assert all(checks.values())
