package com.ashes.crown

/** A political pressure point inferred from the campaign's existing reputation tracks. */
internal data class FactionAgendaItem(
    val id: String,
    val title: String,
    val source: String,
    val pressure: String,
    val opportunity: String,
    val urgency: Int
)

/**
 * Turns reputation combinations into readable political agendas. This layer is deliberately
 * derived-only: it introduces no new save fields and therefore remains compatible with every
 * existing campaign save.
 */
internal object FactionAgendaManager {
    fun evaluate(crown: Int, merchants: Int, people: Int, rose: Int): List<FactionAgendaItem> {
        val c = FactionManager.normalize(crown)
        val m = FactionManager.normalize(merchants)
        val p = FactionManager.normalize(people)
        val r = FactionManager.normalize(rose)
        return buildList {
            if (c >= 20) add(FactionAgendaItem(
                "crown_order", "Порядок превыше всего", "Королевский двор",
                "Двор ждёт от Эдриана доказательств верности и осторожности в делах, способных пошатнуть власть.",
                "Открытая поддержка закона укрепит доверие двора, но может осложнить отношения с теми, кто действует из тени.",
                urgency = if (c >= 60) 3 else 2
            ))
            if (m >= 20) add(FactionAgendaItem(
                "merchant_routes", "Безопасность торговых путей", "Торговые гильдии",
                "Купцы рассчитывают, что герой не позволит конфликтам и произволу остановить поставки.",
                "Защита караванов помогает рынкам и казне, однако требует времени и ресурсов.",
                urgency = if (m >= 60) 3 else 1
            ))
            if (p < 20) add(FactionAgendaItem(
                "people_relief", "Голос улиц", "Жители Эстервала",
                "Народная поддержка непрочна: горожане ждут справедливости, помощи и понятных решений.",
                "Забота о простых людях способна вернуть доверие, даже если это не понравится богатым домам.",
                urgency = if (p <= -50) 3 else 2
            ))
            if (r >= 20) add(FactionAgendaItem(
                "rose_secrecy", "Цена тайны", "Чёрная роза",
                "Орден ценит полезность и молчание; лишняя огласка может превратить союзника в угрозу.",
                "Сдержанность сохраняет доступ к тайным сведениям, но ограничивает возможность действовать открыто.",
                urgency = if (r >= 60) 3 else 2
            ))
            if (c >= 40 && r >= 40) add(FactionAgendaItem(
                "crown_rose_conflict", "Две стороны одной тайны", "Двор и Чёрная роза",
                "Доверие двора и влияние тайного ордена одновременно ставят героя между несовместимыми ожиданиями.",
                "Любое решение может сохранить один канал влияния и поставить под угрозу другой.",
                urgency = 3
            ))
            if (m - p >= 40) add(FactionAgendaItem(
                "wealth_people_gap", "Цена процветания", "Гильдии и горожане",
                "Торговые интересы заметно опережают народное доверие; выгода рынка не обязательно доходит до улиц.",
                "Поиск честного компромисса может укрепить обе стороны, но быстрый путь обычно выгоден лишь одной.",
                urgency = 2
            ))
        }.sortedWith(compareByDescending<FactionAgendaItem> { it.urgency }.thenBy { it.id })
    }
}
