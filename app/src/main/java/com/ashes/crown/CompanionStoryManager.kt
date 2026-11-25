package com.ashes.crown

/** A persistent chapter in one companion's personal storyline. */
internal data class CompanionStoryChapter(
    val companion: String,
    val stage: Int,
    val title: String,
    val scene: String,
    val supportChoice: String,
    val investigateChoice: String,
    val minimumBond: Int
)

internal enum class CompanionStoryChoice { SUPPORT, INVESTIGATE }

internal data class CompanionStoryResult(
    val companion: String,
    val previousStage: Int,
    val nextStage: Int,
    val bondAfter: Int,
    val bondDelta: Int,
    val report: String,
    val completed: Boolean
)

/** Three-chapter personal arcs. The manager is pure; the Activity persists each companion's stage. */
internal object CompanionStoryManager {
    const val LAST_STAGE = 3

    private val chapters = mapOf(
        "Гаррет Морн" to listOf(
            CompanionStoryChapter("Гаррет Морн", 0, "Сломанный ремень", "Гаррет признаётся, что старый ремень принадлежал его погибшему командиру. Он боится, что память о нём стала оправданием собственных ошибок.", "Помочь восстановить ремень", "Спросить, что случилось с командиром", 0),
            CompanionStoryChapter("Гаррет Морн", 1, "Приказ, который не был исполнен", "Гаррет рассказывает о приказе отступить, который он однажды ослушался ради раненых солдат.", "Сказать, что спасённые жизни важнее приказа", "Разобрать последствия его решения", 10),
            CompanionStoryChapter("Гаррет Морн", 2, "Имя на старом щите", "Гаррет хочет вернуть имя командира на памятный щит, но опасается, что двор сочтёт это признанием старой ошибки.", "Встать рядом с ним публично", "Сначала найти свидетелей тех событий", 20)
        ),
        "Селеста" to listOf(
            CompanionStoryChapter("Селеста", 0, "Письмо без подписи", "Селеста обнаружила письмо, связанное с исчезнувшей ветвью её рода. Она не уверена, кому можно доверить эту находку.", "Пообещать хранить её тайну", "Вместе изучить печать письма", 0),
            CompanionStoryChapter("Селеста", 1, "Печать старого дома", "След на печати указывает на человека, которого Селеста считала предателем семьи.", "Не осуждать её сомнения", "Проверить сведения до выводов", 10),
            CompanionStoryChapter("Селеста", 2, "Правда за фамильным гербом", "Селеста готова раскрыть правду о семейном соглашении, но это может лишить её последней опоры при дворе.", "Поддержать её независимо от последствий", "Подготовить безопасный путь раскрытия", 20)
        ),
        "Мира" to listOf(
            CompanionStoryChapter("Мира", 0, "Долг на рынке", "Мира узнаёт среди должников торговца, который когда-то помог её семье. Теперь он просит скрыть недостачу.", "Помочь найти честный выход", "Выяснить, кто воспользовался его доверием", 0),
            CompanionStoryChapter("Мира", 1, "Цена доброты", "Мира признаётся, что часто берёт чужие заботы на себя, даже когда это вредит её собственным делам.", "Напомнить, что она тоже заслуживает помощи", "Предложить установить чёткие границы", 10),
            CompanionStoryChapter("Мира", 2, "Дом у северных ворот", "Семьи торгового квартала собирают средства на приют. Мира хочет вложить туда все свои сбережения.", "Помочь ей открыть приют", "Сначала составить надёжный план снабжения", 20)
        ),
        "Томас" to listOf(
            CompanionStoryChapter("Томас", 0, "Карта с выжженным краем", "Томас узнаёт на карте маршрут корабля, с которого он когда-то не вернулся домой.", "Дать ему время рассказать самому", "Спросить, кто изменил маршрут", 0),
            CompanionStoryChapter("Томас", 1, "Штормовое имя", "Томас вспоминает капитана, который оставил часть команды в шторм. Он до сих пор не знает, был ли это расчёт или паника.", "Не обвинять, пока неизвестны факты", "Найти записи портового журнала", 10),
            CompanionStoryChapter("Томас", 2, "Последний причал", "Старый маяк может хранить доказательство судьбы пропавших моряков. Томас просит отправиться туда вместе.", "Отправиться с ним без колебаний", "Подготовить экспедицию и проверить приливы", 20)
        )
    )

    fun normalizeStage(stage: Int): Int = stage.coerceIn(0, LAST_STAGE)

    fun chapter(companion: String, stage: Int): CompanionStoryChapter? {
        val name = CompanionManager.normalizeCompanion(companion)
        val normalized = normalizeStage(stage)
        return chapters.getValue(name).getOrNull(normalized)
    }

    fun resolve(companion: String, stage: Int, currentBond: Int, choice: CompanionStoryChoice): CompanionStoryResult {
        val name = CompanionManager.normalizeCompanion(companion)
        val current = normalizeStage(stage)
        val chapter = chapters.getValue(name).getOrNull(current)
            ?: return CompanionStoryResult(name, LAST_STAGE, LAST_STAGE, CompanionManager.normalizeBond(currentBond), 0,
                "Личная история $name завершена. Больше не осталось невысказанных глав.", true)
        require(CompanionManager.normalizeBond(currentBond) >= chapter.minimumBond) {
            "Для продолжения истории недостаточно доверия."
        }
        val delta = when (choice) {
            CompanionStoryChoice.SUPPORT -> 3
            CompanionStoryChoice.INVESTIGATE -> if (name == "Гаррет Морн" || name == "Томас") 2 else 1
        }
        val response = when (choice) {
            CompanionStoryChoice.SUPPORT -> "Ты поддержал $name. ${chapter.title}: доверие стало крепче."
            CompanionStoryChoice.INVESTIGATE -> "Вы решили искать ответы вместе. ${chapter.title}: осторожность помогла увидеть новые детали."
        }
        val next = (current + 1).coerceAtMost(LAST_STAGE)
        val completion = next == LAST_STAGE
        val report = if (completion) "$response Личная история $name завершена." else "$response Однажды вы сможете вернуться к следующей главе."
        return CompanionStoryResult(name, current, next, CompanionManager.adjustBond(currentBond, delta), delta, report, completion)
    }
}
