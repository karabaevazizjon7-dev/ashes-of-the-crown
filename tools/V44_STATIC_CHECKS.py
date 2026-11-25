from pathlib import Path
root = Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/ashes/crown/MainActivity.kt').read_text(encoding='utf-8')
clock = (root / 'app/src/main/java/com/ashes/crown/WorldClockManager.kt').read_text(encoding='utf-8')
tests = (root / 'tools/WorldClockRulesCheck.kt').read_text(encoding='utf-8')
checks = {
    'clock arithmetic is isolated in a pure Kotlin manager': 'internal object WorldClockManager' in clock,
    'advance result exposes normalized day and hour': all(x in clock for x in ('targetDay: Int', 'targetHour: Int', 'pendingUpkeepDays: Int')),
    'time phases are centrally defined': 'internal enum class WorldTimePhase' in clock,
    'clock arithmetic uses Long to avoid Int overflow': 'totalHours = safeHour.toLong() + elapsed.toLong()' in clock,
    'Compose time advancement delegates to clock manager': 'WorldClockManager.advance(' in main,
    'daily settlement loop remains in the existing deterministic sequence': 'repeat(clockAdvance.pendingUpkeepDays)' in main,
    'MainActivity no longer duplicates hour/day arithmetic': 'val total = worldHour + hours.coerceAtLeast(0)' not in main,
    'MainActivity uses manager-provided phase and message': all(x in main for x in ('clockAdvance.phase.label', 'clockAdvance.timeMessage')),
    'clock boundary and overflow regression coverage exists': all(x in tests for x in ('sunrise', 'dusk', 'Int.MAX_VALUE', 'severalDays')),
}
for label, ok in checks.items():
    print(('PASS' if ok else 'FAIL') + ': ' + label)
assert all(checks.values())
