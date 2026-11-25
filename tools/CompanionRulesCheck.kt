package com.ashes.crown

fun main() {
    check(CompanionManager.roster == listOf("Гаррет Морн", "Селеста", "Мира", "Томас"))
    check(CompanionManager.normalizeBond(-500) == -100)
    check(CompanionManager.normalizeBond(500) == 100)

    val garrett = CompanionManager.expedition("Гаррет Морн", 10)
    check(garrett.healthDelta == -3 && garrett.goldDelta == 0)
    check(garrett.bondAfter == 12 && garrett.itemReward == "Затупленный наконечник")
    check(!garrett.itemIsUnique && garrett.report.contains("Здоровье −3"))

    val celeste = CompanionManager.expedition("Селеста", 10)
    check(celeste.influenceDelta == 3 && celeste.bondAfter == 13)
    check(celeste.itemReward == "Запечатанное письмо дома" && celeste.itemIsUnique)

    val mira = CompanionManager.expedition("Мира", 10)
    check(mira.goldDelta == 4 && mira.bondAfter == 12)
    check(mira.itemReward == "Дорожные припасы" && !mira.itemIsUnique)

    val thomas = CompanionManager.expedition("Томас", 10)
    check(thomas.roseReputationDelta == 2 && thomas.bondAfter == 12)
    check(thomas.itemReward == "Морская карта Веймара" && !thomas.itemIsUnique)

    // Personal moments are character-specific and have bounded, explicit outcomes.
    val garrettMoment = CompanionManager.personalMoment("Гаррет Морн")
    check(garrettMoment.preferredApproach == CompanionApproach.SUPPORT)
    val garrettResponse = CompanionManager.resolvePersonalMoment("Гаррет Морн", CompanionApproach.SUPPORT, 10)
    check(garrettResponse.bondAfter == 13 && garrettResponse.bondDelta == 3)
    val celesteMoment = CompanionManager.personalMoment("Селеста")
    check(celesteMoment.preferredApproach == CompanionApproach.ASK_ABOUT_PAST)
    check(CompanionManager.resolvePersonalMoment("Селеста", CompanionApproach.ASK_ABOUT_PAST, 98).bondAfter == 100)
    val miraMoment = CompanionManager.personalMoment("Мира")
    check(miraMoment.preferredApproach == CompanionApproach.HUMOR)
    val awkwardMira = CompanionManager.resolvePersonalMoment("Мира", CompanionApproach.ASK_ABOUT_PAST, 10)
    check(awkwardMira.bondAfter == 9 && awkwardMira.bondDelta == -1)
    check(CompanionManager.personalMoment("Томас").preferredApproach == CompanionApproach.ASK_ABOUT_PAST)
    check(CompanionManager.resolvePersonalMoment("Томас", CompanionApproach.SUPPORT, -100).bondAfter == -99)

    val migratedBonds = CompanionManager.migrateLegacyBonds(
        "Мира", 37,
        mapOf("Гаррет Морн" to null, "Селеста" to 12, "Мира" to null, "Томас" to -4)
    )
    check(migratedBonds == mapOf("Гаррет Морн" to 0, "Селеста" to 12, "Мира" to 37, "Томас" to -4))

    // Corrupt/unknown legacy companion value follows the historical fallback to Thomas.
    val fallback = CompanionManager.expedition("Неизвестный спутник", 99)
    check(fallback.companionName == "Томас" && fallback.roseReputationDelta == 2)
    check(fallback.bondAfter == 100)

    // Bond cannot escape the supported persisted range.
    check(CompanionManager.expedition("Селеста", 100).bondAfter == 100)
    check(CompanionManager.expedition("Гаррет Морн", -100).bondAfter == -98)

    check(CompanionManager.adjustBond(Int.MAX_VALUE, 3) == 100)
    check(CompanionManager.adjustBond(Int.MIN_VALUE, -3) == -100)
    println("Companion roster, personal moments, per-companion bond migration, expedition outcomes and bond-boundary checks passed")
}
