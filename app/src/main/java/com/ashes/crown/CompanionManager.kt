package com.ashes.crown

/** Immutable result of one companion expedition; UI applies the deltas to Compose state. */
internal data class CompanionExpeditionResult(
    val companionName: String,
    val healthDelta: Int = 0,
    val goldDelta: Int = 0,
    val influenceDelta: Int = 0,
    val roseReputationDelta: Int = 0,
    val bondAfter: Int,
    val itemReward: String,
    val itemIsUnique: Boolean,
    val report: String
)

internal enum class CompanionApproach { SUPPORT, ASK_ABOUT_PAST, HUMOR }

internal data class CompanionPersonalMoment(
    val companionName: String,
    val prompt: String,
    val preferredApproach: CompanionApproach,
    val supportLabel: String,
    val curiosityLabel: String,
    val humorLabel: String
)

internal data class CompanionMomentResult(
    val companionName: String,
    val approach: CompanionApproach,
    val bondAfter: Int,
    val bondDelta: Int,
    val response: String
)

/** Pure companion rules. Individual bonds are persisted by the Activity under schema 8. */
internal object CompanionManager {
    const val MIN_BOND = -100
    const val MAX_BOND = 100

    val roster: List<String> = listOf("Гаррет Морн", "Селеста", "Мира", "Томас")

    private val moments = mapOf(
        "Гаррет Морн" to CompanionPersonalMoment(
            "Гаррет Морн", "Гаррет проверяет ремни старого доспеха. Он редко говорит о прошлом, но сегодня явно хочет услышать твоё мнение.",
            CompanionApproach.SUPPORT, "Предложить помощь", "Спросить о прошлом", "Разрядить разговор шуткой"
        ),
        "Селеста" to CompanionPersonalMoment(
            "Селеста", "Селеста изучает письмо старого дома и пытается понять, кто оставил в нём скрытое послание.",
            CompanionApproach.ASK_ABOUT_PAST, "Поддержать её решение", "Расспросить о письме", "Пошутить о дворцовых тайнах"
        ),
        "Мира" to CompanionPersonalMoment(
            "Мира", "Мира вернулась с рынка с корзиной припасов и рассказывает забавную историю о слишком самоуверенном торговце.",
            CompanionApproach.HUMOR, "Предложить помощь", "Расспросить о торговце", "Поддержать её шутку"
        ),
        "Томас" to CompanionPersonalMoment(
            "Томас", "Томас разглядывает старую морскую карту. На одном участке чернила стёрты, будто кто-то намеренно скрыл маршрут.",
            CompanionApproach.ASK_ABOUT_PAST, "Предложить помощь", "Расспросить о карте", "Пошутить о морских суевериях"
        )
    )

    fun normalizeBond(value: Int): Int = value.coerceIn(MIN_BOND, MAX_BOND)

    fun adjustBond(current: Int, delta: Int): Int =
        (current.toLong() + delta.toLong()).coerceIn(MIN_BOND.toLong(), MAX_BOND.toLong()).toInt()

    fun normalizeCompanion(name: String): String = name.takeIf { it in roster } ?: "Томас"

    fun personalMoment(companion: String): CompanionPersonalMoment =
        moments.getValue(normalizeCompanion(companion))

    fun resolvePersonalMoment(
        companion: String,
        approach: CompanionApproach,
        currentBond: Int
    ): CompanionMomentResult {
        val name = normalizeCompanion(companion)
        val moment = moments.getValue(name)
        val delta = when (approach) {
            moment.preferredApproach -> 3
            CompanionApproach.HUMOR -> if (name == "Гаррет Морн" || name == "Селеста") 1 else 1
            else -> if (name == "Мира" && approach == CompanionApproach.ASK_ABOUT_PAST) -1 else 1
        }
        val response = when {
            delta == 3 -> when (approach) {
                CompanionApproach.SUPPORT -> "$name ценит, что ты не стал отмахиваться от его слов. Между вами стало больше доверия."
                CompanionApproach.ASK_ABOUT_PAST -> "$name впервые открывает тебе часть своей истории. Разговор сближает вас."
                CompanionApproach.HUMOR -> "$name смеётся вместе с тобой. Напряжение исчезает, и разговор становится по-настоящему дружеским."
            }
            delta < 0 -> "Мира предпочла бы сейчас не возвращаться к этой истории. Разговор вышел неловким, но она не стала ссориться."
            else -> "$name спокойно принимает твой ответ. Разговор прошёл доброжелательно, хотя важного признания сегодня не случилось."
        }
        return CompanionMomentResult(name, approach, adjustBond(currentBond, delta), delta, response)
    }

    fun migrateLegacyBonds(selectedCompanion: String, legacyBond: Int, stored: Map<String, Int?>): Map<String, Int> {
        val selected = normalizeCompanion(selectedCompanion)
        return roster.associateWith { name ->
            normalizeBond(stored[name] ?: if (name == selected) legacyBond else 0)
        }
    }

    fun expedition(companion: String, currentBond: Int): CompanionExpeditionResult {
        val name = normalizeCompanion(companion)
        val bond = normalizeBond(currentBond)
        return when (name) {
            "Гаррет Морн" -> CompanionExpeditionResult(
                companionName = name, healthDelta = -3, bondAfter = normalizeBond(bond + 2),
                itemReward = "Затупленный наконечник", itemIsUnique = false,
                report = "Гаррет заметил засаду раньше тебя. Вы избежали тяжёлого боя, но нашли лишь наконечник старой стрелы. Здоровье −3."
            )
            "Селеста" -> CompanionExpeditionResult(
                companionName = name, influenceDelta = 3, bondAfter = normalizeBond(bond + 3),
                itemReward = "Запечатанное письмо дома", itemIsUnique = true,
                report = "Селеста разговорила сторожа старого тракта. Ты получил запечатанное письмо и сведения о тайной встрече. Влияние +3."
            )
            "Мира" -> CompanionExpeditionResult(
                companionName = name, goldDelta = 4, bondAfter = normalizeBond(bond + 2),
                itemReward = "Дорожные припасы", itemIsUnique = false,
                report = "Мира нашла короткий путь через торговые дворы. Вы вернулись с припасами и четырьмя золотыми. Золото +4."
            )
            else -> CompanionExpeditionResult(
                companionName = name, roseReputationDelta = 2, bondAfter = normalizeBond(bond + 2),
                itemReward = "Морская карта Веймара", itemIsUnique = false,
                report = "Томас провёл тебя к заброшенному причалу. В тайнике лежала морская карта с отметкой тайного маршрута. Влияние ордена +2."
            )
        }
    }
}
