package com.ashes.crown

/** A derived council recommendation joining economic, military, provincial and political state. */
internal data class RealmPriority(
    val id: String,
    val title: String,
    val reason: String,
    val recommendation: String,
    val urgency: Int
)

/**
 * Produces deterministic, read-only priorities from existing campaign values.
 * It deliberately does not mutate resources or persist flags: recommendations update
 * immediately when the underlying campaign state changes.
 */
internal object RealmPriorityManager {
    fun evaluate(
        gold: Int,
        provisions: Int,
        dailyRations: Int,
        army: Int,
        guard: Int,
        provinceNames: List<String>,
        provinceLoyalty: List<Int>,
        provinceCrisisDays: List<Int>,
        crownRep: Int,
        merchantRep: Int,
        peopleRep: Int,
        roseRep: Int
    ): List<RealmPriority> {
        val priorities = mutableListOf<RealmPriority>()
        val daysOfSupply = if (dailyRations <= 0) Int.MAX_VALUE else provisions.coerceAtLeast(0) / dailyRations
        if (dailyRations > 0 && daysOfSupply <= 2) priorities += RealmPriority(
            "army_supply", "Снабжение королевского войска",
            "Провианта хватит приблизительно на $daysOfSupply дн.",
            "Пополнить запасы до следующего похода; иначе ежедневное содержание приведёт к потерям.",
            if (daysOfSupply == 0) 3 else 2
        )
        if (gold < 12) priorities += RealmPriority(
            "treasury", "Истощённая казна",
            "В казне $gold золота — средств недостаточно даже для базовой помощи провинции.",
            "Отложить необязательные расходы и восстановить доходы владений.",
            if (gold <= 3) 3 else 2
        )
        val count = minOf(provinceNames.size, provinceLoyalty.size, provinceCrisisDays.size)
        val unstable = (0 until count).filter { provinceCrisisDays[it] > 0 || provinceLoyalty[it] < 25 }
        if (unstable.isNotEmpty()) {
            val worst = unstable.maxByOrNull { provinceCrisisDays[it] * 100 + (100 - provinceLoyalty[it]) }!!
            val crisis = provinceCrisisDays[worst].coerceIn(0, 3)
            priorities += RealmPriority(
                "province_stability", "Угроза провинциальной стабильности",
                "Требует внимания: ${provinceNames[worst]} (верность ${provinceLoyalty[worst].coerceIn(0, 100)}/100, кризис ${crisis}/3).",
                "Рассмотреть помощь или переговоры до того, как недовольство перерастёт в восстание.",
                if (crisis >= 2 || provinceLoyalty[worst] < 10) 3 else 2
            )
        }
        if (guard < 25 && army >= 20) priorities += RealmPriority(
            "guard_confidence", "Падает доверие гвардии",
            "Доверие гвардии составляет ${guard.coerceIn(0, 100)}/100 при численности войска $army.",
            "Провести учения и обеспечить войско; численность без дисциплины ненадёжна.",
            if (guard < 10) 3 else 1
        )
        if (peopleRep <= -30 && crownRep >= 30) priorities += RealmPriority(
            "legitimacy_gap", "Разрыв между двором и улицами",
            "Двор поддерживает корону, но народная репутация опустилась до $peopleRep.",
            "Сочетать законность решений с видимой помощью жителям, чтобы не усиливать отчуждение.",
            2
        )
        if (merchantRep <= -25 && gold < 30) priorities += RealmPriority(
            "trade_pressure", "Торговля и казна под давлением",
            "Недоверие гильдий совпало с ограниченными запасами казны.",
            "Не обострять торговые споры; восстановление маршрутов может поддержать доходы.",
            2
        )
        if (roseRep >= 50 && crownRep >= 50) priorities += RealmPriority(
            "double_loyalty", "Два центра влияния",
            "Высокое доверие одновременно к двору и Чёрной розе повышает цену публичных решений.",
            "Проверять последствия раскрытия сведений и избегать обещаний, которые нельзя выполнить обеим сторонам.",
            1
        )
        return priorities.sortedWith(compareByDescending<RealmPriority> { it.urgency }.thenBy { it.id }).take(4)
    }
}
