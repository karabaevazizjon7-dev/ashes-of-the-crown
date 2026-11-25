package com.ashes.crown

fun main() {
    val maxScene = 18
    val cases = listOf(
        Triple("gold", Int.MIN_VALUE, 0),
        Triple("gold", Int.MAX_VALUE, 1_000_000),
        Triple("hp", -900, 0),
        Triple("hp", 900, 100),
        Triple("rep", Int.MIN_VALUE, -100),
        Triple("rep", Int.MAX_VALUE, 100),
        Triple("guard", -1, 0),
        Triple("guard", Int.MAX_VALUE, 100),
        Triple("experience", Int.MAX_VALUE, 1_000_000),
        Triple("hero_level", 0, 1),
        Triple("hero_level", Int.MAX_VALUE, 1_000),
        Triple("skill_points", Int.MAX_VALUE, 100_000),
        Triple("skill_power", -1, 0),
        Triple("skill_power", Int.MAX_VALUE, 10),
        Triple("encounters", Int.MIN_VALUE, 0),
        Triple("encounters", Int.MAX_VALUE, 1_000_000),
        Triple("mem_garrett", Int.MAX_VALUE, 1_000_000),
        Triple("crafted_count", Int.MIN_VALUE, 0),
        Triple("social_event_flags", Int.MAX_VALUE, 255),
        Triple("social_action_flags", -1, 0),
        Triple("scene", Int.MAX_VALUE, maxScene)
    )
    cases.forEach { (key, input, expected) ->
        check(SaveManager.normalizeInteger(key, input, maxScene) == expected) {
            "$key did not normalize $input to $expected"
        }
    }
    check(SaveManager.normalizeInteger("weather_seed", Int.MIN_VALUE, maxScene) == Int.MIN_VALUE) {
        "Unbounded opaque seed must remain unchanged"
    }
    println("Legacy preference bounds, extreme integers, resource limits, counters and opaque-field preservation passed")
}
