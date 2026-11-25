package com.ashes.crown

fun main() {
    val report = WorldCrisisManager.evaluate(
        names = listOf("Север", "Равнины", "Порт", "Лесной край"),
        loyalty = listOf(80, 18, 12, 4),
        prosperity = listOf(75, 45, 20, 10),
        crisisDays = listOf(0, 1, 2, 3)
    )
    check(report.provinces.map { it.stage } == listOf(
        ProvinceCrisisStage.STABLE, ProvinceCrisisStage.UNREST,
        ProvinceCrisisStage.ESCALATING, ProvinceCrisisStage.REVOLT
    ))
    check(report.activeCount == 3 && report.revoltCount == 1)
    check(report.mostUrgent?.name == "Лесной край")
    check(report.provinces.all { it.recommendedAction.isNotBlank() })
    check(report.summary.contains("Восстание"))

    val calm = WorldCrisisManager.evaluate(listOf("Север"), listOf(100), listOf(100), listOf(0))
    check(calm.activeCount == 0 && calm.revoltCount == 0 && calm.mostUrgent == null)
    check(calm.summary.contains("спокойны"))

    val mismatched = WorldCrisisManager.evaluate(
        listOf("A", "B"), listOf(50), listOf(40, 20), listOf(1, 3)
    )
    check(mismatched.provinces.size == 1)

    val extreme = WorldCrisisManager.evaluate(
        listOf("X"), listOf(Int.MIN_VALUE), listOf(Int.MAX_VALUE), listOf(Int.MAX_VALUE)
    )
    check(extreme.provinces.single().stage == ProvinceCrisisStage.REVOLT)
    check(extreme.provinces.single().loyalty == 0)
    check(extreme.provinces.single().prosperity == 100)
    println("Province crisis stages, urgency ranking, recovery guidance and boundary handling passed")
}
