from pathlib import Path
root = Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text(encoding='utf-8')
pipeline = (root / 'tools/RealmDayPipelineRulesCheck.kt').read_text(encoding='utf-8')
clock = (root / 'app/src/main/java/com/ashes/crown/WorldClockManager.kt').read_text(encoding='utf-8')
realm = (root / 'app/src/main/java/com/ashes/crown/RealmDayProcessor.kt').read_text(encoding='utf-8')
checks = {
    'daily settlement uses the persisted upkeep marker': 'a14_upkeep_day' in main and 'lastUpkeepDay++' in main,
    'province income is independently guarded by its day marker': 'provinceIncomeDay < lastUpkeepDay' in main and 'provinceIncomeDay = lastUpkeepDay' in main,
    'daily economy is settled before crisis transitions': main.index('RealmDayProcessor.processDay(') < main.index('CrisisConsequenceManager.resolve('),
    'crisis transitions are applied before daily world event selection': main.index('CrisisConsequenceManager.resolve(') < main.index('WorldEventsManager.resolve('),
    'world event effects are applied before the next day iteration': main.index('WorldEventsManager.resolve(') < main.index('if (dayResult.supplied)'),
    'realm settlement remains pure and deterministic': 'internal object RealmDayProcessor' in realm and 'fun processDay(' in realm,
    'clock uses bounded long arithmetic': 'totalHours = safeHour.toLong() + elapsed.toLong()' in clock,
    'pipeline regression covers split advances and reload marker': all(x in pipeline for x in ('firstAdvance', 'secondAdvance', 'processedEventDays.distinct()', 'replay.pendingUpkeepDays == 0')),
    'pipeline regression asserts one economy tick per settled day': 'economyDays == 6 && provinceIncomeDay == 7' in pipeline,
}
for label, ok in checks.items():
    print(('PASS' if ok else 'FAIL') + ': ' + label)
assert all(checks.values())
