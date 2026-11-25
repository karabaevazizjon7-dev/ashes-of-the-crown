package com.ashes.crown

/** A one-time cooperative scene unlocked by a healthy bond between two companions. */
internal data class CompanionTeamEvent(
    val id: Int,
    val pair: CompanionPair,
    val title: String,
    val scene: String,
    val cooperativeChoice: String,
    val pragmaticChoice: String,
    val minimumPairAffinity: Int = 10,
    val minimumHeroBond: Int = 5
)

internal enum class CompanionTeamEventChoice { COOPERATE, COMPROMISE }

internal data class CompanionTeamEventResult(
    val eventId: Int,
    val pairAffinityAfter: Int,
    val firstHeroBondAfter: Int,
    val secondHeroBondAfter: Int,
    val report: String
)

/** Pure rules for unique two-companion scenes. Completion is persisted by the Activity bit mask. */
internal object CompanionTeamEventManager {
    const val MAX_EVENT_ID = 5
    const val ALL_EVENTS_MASK = (1 shl (MAX_EVENT_ID + 1)) - 1

    val events = listOf(
        CompanionTeamEvent(0, CompanionPair.GARRETT_CELESTE, "План на двоих", "Гаррет предлагает прямой штурм старого склада, а Селеста замечает на плане скрытый служебный вход. Впервые они готовы объединить дисциплину и расчёт.", "Довериться их общему плану", "Попросить их проверить слабые места плана"),
        CompanionTeamEvent(1, CompanionPair.GARRETT_MIRA, "Щит для обозa", "Мира хочет сопровождать раненых переселенцев, а Гаррет предлагает перестроить охрану так, чтобы никто не остался без защиты.", "Организовать совместный караул", "Сначала распределить припасы и обязанности"),
        CompanionTeamEvent(2, CompanionPair.GARRETT_THOMAS, "Лагерь у прилива", "Гаррет выбирает место для лагеря по военным правилам. Томас замечает, что через час сюда придёт прилив, и предлагает безопасную площадку.", "Соединить военный порядок с морским опытом", "Проверить местность до окончательного решения"),
        CompanionTeamEvent(3, CompanionPair.CELESTE_MIRA, "Письмо и свидетель", "Селеста готова раскрыть часть семейной тайны, но боится навредить невиновным. Мира находит человека, который может подтвердить факты.", "Вместе выслушать свидетеля", "Сначала защитить свидетеля и проверить письмо"),
        CompanionTeamEvent(4, CompanionPair.CELESTE_THOMAS, "Координаты на полях", "Томас узнаёт на карте отметку старого маяка. Селеста связывает её с архивной записью, которую раньше считала несущественной.", "Сверить карту и архив вместе", "Составить безопасный маршрут и только потом идти"),
        CompanionTeamEvent(5, CompanionPair.MIRA_THOMAS, "Последний ящик", "Мира хочет отдать портовым семьям часть запасов. Томас предлагает доставить помощь по воде, не подвергая обоз опасности.", "Организовать общую доставку", "Оставить резерв и отправить небольшую партию")
    )

    fun isCompleted(mask: Int, eventId: Int): Boolean = eventId in 0..MAX_EVENT_ID && (mask and (1 shl eventId)) != 0
    fun markCompleted(mask: Int, eventId: Int): Int {
        require(eventId in 0..MAX_EVENT_ID)
        return (mask or (1 shl eventId)) and ALL_EVENTS_MASK
    }

    fun isUnlocked(event: CompanionTeamEvent, pairAffinity: Int, firstHeroBond: Int, secondHeroBond: Int): Boolean =
        pairAffinity >= event.minimumPairAffinity && firstHeroBond >= event.minimumHeroBond && secondHeroBond >= event.minimumHeroBond

    fun resolve(
        event: CompanionTeamEvent,
        pairAffinity: Int,
        firstHeroBond: Int,
        secondHeroBond: Int,
        choice: CompanionTeamEventChoice
    ): CompanionTeamEventResult {
        require(isUnlocked(event, pairAffinity, firstHeroBond, secondHeroBond)) { "Команда пока не готова к этому совместному событию." }
        val affinityDelta = if (choice == CompanionTeamEventChoice.COOPERATE) 6 else 2
        val firstDelta = 2
        val secondDelta = 2
        val report = when (choice) {
            CompanionTeamEventChoice.COOPERATE -> "${event.pair.first} и ${event.pair.second} нашли общий язык. Их совместное дело укрепило команду."
            CompanionTeamEventChoice.COMPROMISE -> "Ты помог ${event.pair.first} и ${event.pair.second} договориться без лишнего риска. Каждый сохранил собственный подход."
        }
        return CompanionTeamEventResult(
            event.id,
            CompanionManager.adjustBond(pairAffinity, affinityDelta),
            CompanionManager.adjustBond(firstHeroBond, firstDelta),
            CompanionManager.adjustBond(secondHeroBond, secondDelta),
            report
        )
    }
}
