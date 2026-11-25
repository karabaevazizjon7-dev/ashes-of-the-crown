package com.ashes.crown

/** Boundary and long-run progression checks aligned with SaveManager's persisted ranges. */
object ProgressionIntegrityRulesCheck {
    @JvmStatic fun main(args: Array<String>) {
        val normalized = PlayerProgressManager.normalize(
            PlayerProgressState(level = Int.MAX_VALUE, experience = Int.MAX_VALUE, skillPoints = Int.MAX_VALUE)
        )
        check(normalized == PlayerProgressState(1_000, 1_000_000, 100_000))
        check(PlayerProgressManager.normalize(PlayerProgressState(-9, -5, -1)) == PlayerProgressState(1, 0, 0))

        val noReward = PlayerProgressManager.grantExperience(
            PlayerProgressState(10, 250, 12), 0
        )
        check(noReward.state == PlayerProgressState(10, 250, 12))
        check(noReward.levelsGained == 0 && noReward.message == null)

        val negativeReward = PlayerProgressManager.grantExperience(
            PlayerProgressState(10, 250, 12), -500
        )
        check(negativeReward.state == PlayerProgressState(10, 250, 12))

        val ordinary = PlayerProgressManager.grantExperience(
            PlayerProgressState(1, 0, 0), 250
        )
        check(ordinary.state == PlayerProgressState(2, 150, 1))
        check(ordinary.levelsGained == 1)
        check(ordinary.state.skillPoints == 1)

        val levelCap = PlayerProgressManager.grantExperience(
            PlayerProgressState(999, 99_900, 99_999), 1_000_000
        )
        check(levelCap.state.level == PlayerProgressManager.MAX_LEVEL)
        check(levelCap.state.experience in 0..PlayerProgressManager.MAX_EXPERIENCE)
        check(levelCap.state.skillPoints == PlayerProgressManager.MAX_SKILL_POINTS)
        check(levelCap.levelsGained == 1)

        val xpCap = PlayerProgressManager.grantExperience(
            PlayerProgressState(1, 999_999, 0), Int.MAX_VALUE
        )
        check(xpCap.state.level in 1..PlayerProgressManager.MAX_LEVEL)
        check(xpCap.state.experience in 0..PlayerProgressManager.MAX_EXPERIENCE)
        check(xpCap.state.skillPoints in 0..PlayerProgressManager.MAX_SKILL_POINTS)

        val cappedPoints = PlayerProgressManager.grantExperience(
            PlayerProgressState(1, 0, 100_000), 100
        )
        check(cappedPoints.state.skillPoints == 100_000)
        println("ProgressionIntegrityRules: normalization, XP boundaries, level cap and skill-point cap passed")
    }
}
