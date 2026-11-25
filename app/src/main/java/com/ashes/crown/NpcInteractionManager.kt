package com.ashes.crown

/** Result of checking whether the player can start a face-to-face conversation. */
internal sealed class NpcInteractionResult {
    data class Available(val npcName: String, val location: String) : NpcInteractionResult()
    data class Away(val npcName: String, val currentLocation: String, val npcLocation: String) : NpcInteractionResult()
    data class Busy(val npcName: String, val location: String, val reason: String) : NpcInteractionResult()
}

/**
 * Single gate for face-to-face NPC conversations. It uses the existing time-derived
 * schedule, so interaction availability is not separately persisted and cannot drift
 * away from the world clock. Unscheduled story figures retain a conservative castle
 * fallback until they receive their own timetable.
 */
internal object NpcInteractionManager {
    private const val CASTLE = "Королевский замок"

    fun evaluate(npcName: String, playerLocation: String, hour: Int): NpcInteractionResult {
        val canonicalPlayerLocation = WorldManager.canonicalName(playerLocation)
        val status = NpcScheduleManager.roster(hour).firstOrNull { it.npcName == npcName }

        if (status == null) {
            val expectedLocation = CASTLE
            return if (canonicalPlayerLocation == expectedLocation) {
                NpcInteractionResult.Available(npcName, expectedLocation)
            } else {
                NpcInteractionResult.Away(npcName, canonicalPlayerLocation, expectedLocation)
            }
        }

        val npcLocation = WorldManager.canonicalName(status.location)
        if (canonicalPlayerLocation != npcLocation) {
            return NpcInteractionResult.Away(npcName, canonicalPlayerLocation, npcLocation)
        }
        if (!status.availableForConversation) {
            return NpcInteractionResult.Busy(npcName, npcLocation, status.activity)
        }
        return NpcInteractionResult.Available(npcName, npcLocation)
    }

    fun isAvailable(npcName: String, playerLocation: String, hour: Int): Boolean =
        evaluate(npcName, playerLocation, hour) is NpcInteractionResult.Available

    fun message(result: NpcInteractionResult): String = when (result) {
        is NpcInteractionResult.Available -> "${result.npcName} готов поговорить здесь."
        is NpcInteractionResult.Away -> "${result.npcName} сейчас находится: ${result.npcLocation}. Ты находишься в ${result.currentLocation}. Доберись до него, чтобы поговорить лично."
        is NpcInteractionResult.Busy -> "${result.npcName} сейчас занят: ${result.reason}"
    }
}
