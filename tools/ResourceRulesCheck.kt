package com.ashes.crown

fun main() {
    check(ResourceRules.addGold(999_999, 50) == 1_000_000)
    check(ResourceRules.addGold(1_000_000, Int.MAX_VALUE) == 1_000_000)
    check(ResourceRules.addGold(10, Int.MIN_VALUE) == 0)
    check(ResourceRules.addGold(Int.MAX_VALUE, 1) == 1_000_000)
    check(ResourceRules.addGold(Int.MIN_VALUE, 1) == 0)
    check(ResourceRules.addInfluence(99, 50) == 100)
    check(ResourceRules.addInfluence(0, Int.MIN_VALUE) == 0)
    println("ResourceRules: bounded gold/influence arithmetic and Int overflow boundaries passed")
}
