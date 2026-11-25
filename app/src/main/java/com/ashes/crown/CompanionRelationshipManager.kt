package com.ashes.crown

/** Persistent social bonds between companions; independent from the hero's bond with each member. */
internal enum class CompanionPair(val key: String, val first: String, val second: String) {
    GARRETT_CELESTE("garrett_celeste", "Гаррет Морн", "Селеста"),
    GARRETT_MIRA("garrett_mira", "Гаррет Морн", "Мира"),
    GARRETT_THOMAS("garrett_thomas", "Гаррет Морн", "Томас"),
    CELESTE_MIRA("celeste_mira", "Селеста", "Мира"),
    CELESTE_THOMAS("celeste_thomas", "Селеста", "Томас"),
    MIRA_THOMAS("mira_thomas", "Мира", "Томас")
}

internal enum class CompanionRelationshipChoice { MEDIATE, SUPPORT_FIRST, SUPPORT_SECOND }

internal data class CompanionRelationshipScene(
    val pair: CompanionPair,
    val title: String,
    val description: String
)

internal data class CompanionRelationshipResult(
    val pair: CompanionPair,
    val firstScore: Int,
    val secondScore: Int,
    val report: String
)

internal object CompanionRelationshipManager {
    const val MIN_SCORE = -100
    const val MAX_SCORE = 100
    val pairs: List<CompanionPair> = CompanionPair.values().toList()

    private val scenes = mapOf(
        CompanionPair.GARRETT_CELESTE to ("Приказ и расчёт" to "Гаррет считает, что Селеста слишком долго взвешивает решения. Она отвечает, что его поспешность однажды едва не стоила жизни отряду."),
        CompanionPair.GARRETT_MIRA to ("Цена милосердия" to "Мира хочет помочь семье беженцев без оплаты. Гаррет опасается, что без плана помощь поставит под угрозу весь обоз."),
        CompanionPair.GARRETT_THOMAS to ("Сухопутный устав" to "Гаррет требует точного построения лагеря. Томас настаивает, что у берега правила должны учитывать приливы и ветер."),
        CompanionPair.CELESTE_MIRA to ("Тайна и доверие" to "Селеста просит Миру не раскрывать найденное письмо. Мира считает, что молчание может навредить людям, которых затрагивает тайна."),
        CompanionPair.CELESTE_THOMAS to ("Карта без объяснений" to "Томас отказывается отдавать старую карту двору. Селеста подозревает, что на ней отмечено нечто важное для королевства."),
        CompanionPair.MIRA_THOMAS to ("Последний запас" to "Мира хочет передать часть припасов портовым семьям. Томас напоминает, что впереди шторм и команда тоже зависит от этих запасов.")
    )

    fun normalize(score: Int): Int = score.coerceIn(MIN_SCORE, MAX_SCORE)

    fun scene(pair: CompanionPair): CompanionRelationshipScene {
        val content = scenes.getValue(pair)
        return CompanionRelationshipScene(pair, content.first, content.second)
    }

    fun resolve(
        pair: CompanionPair,
        currentFirst: Int,
        currentSecond: Int,
        choice: CompanionRelationshipChoice
    ): CompanionRelationshipResult {
        // One shared affinity value represents the relationship as a whole. Mediation
        // improves it; openly taking sides strains the pair regardless of whom you support.
        val delta = when (choice) {
            CompanionRelationshipChoice.MEDIATE -> 4
            CompanionRelationshipChoice.SUPPORT_FIRST, CompanionRelationshipChoice.SUPPORT_SECOND -> -3
        }
        val firstDelta = delta
        val secondDelta = delta
        val first = CompanionManager.adjustBond(currentFirst, firstDelta)
        val second = CompanionManager.adjustBond(currentSecond, secondDelta)
        val report = when (choice) {
            CompanionRelationshipChoice.MEDIATE -> "Ты помог найти общий язык ${pair.first} и ${pair.second}. Их отношения стали спокойнее."
            CompanionRelationshipChoice.SUPPORT_FIRST -> "Ты поддержал ${pair.first}. ${pair.second} почувствовал(а), что его(её) не услышали."
            CompanionRelationshipChoice.SUPPORT_SECOND -> "Ты поддержал ${pair.second}. ${pair.first} остался(ась) недоволен(на) твоим решением."
        }
        return CompanionRelationshipResult(pair, first, second, report)
    }
}
