from pathlib import Path

root = Path(__file__).resolve().parents[1]
save = (root / "app/src/main/java/com/ashes/crown/SaveManager.kt").read_text(encoding="utf-8")
test = (root / "tools/SaveSchemaBoundaryRulesCheck.kt").read_text(encoding="utf-8")

checks = {
    "schema value is widened to Long before narrowing": "val schemaLong = (schemaValue as? Number)?.toLong() ?: 1L" in save,
    "schema lower and upper Int bounds are checked before toInt": "schemaLong < 1L || schemaLong > Int.MAX_VALUE.toLong()" in save and save.index("schemaLong < 1L") < save.index("val schema = schemaLong.toInt()"),
    "future schema values are rejected before current-schema validation": "schemaLong > CURRENT_SCHEMA.toLong()" in save,
    "regression test covers Int-overflow wrap to current schema": "4_294_967_307L" in test and "must not masquerade as the current schema" in test,
    "regression test covers out-of-range, fractional and negative values": "tooLargeSchema" in test and "fractionalSchema" in test and "negativeSchema" in test,
    "current save schema remains 11": "const val CURRENT_SCHEMA = 11" in save,
}
for label, ok in checks.items():
    print(("PASS" if ok else "FAIL") + ": " + label)
assert all(checks.values())
