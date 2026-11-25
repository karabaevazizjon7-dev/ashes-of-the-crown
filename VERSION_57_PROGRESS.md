# Version 57 verification record

This update fixes a concrete boundary bug in the save-schema parser. The previous `Number.toInt()` conversion could wrap a large `Long` value into the current schema number before compatibility checks. Validation now checks the wide value before narrowing.

Verification performed for this archive:
- Focused regression test covers the exact overflow value `4_294_967_307L` and other invalid schema boundaries.
- Existing static checks V30–V56 are retained and will be rerun alongside V57.
- Pure Kotlin regression suite is compiled and executed where dependencies permit.
- Android/Compose packaging is not claimed unless an Android SDK build is available.
