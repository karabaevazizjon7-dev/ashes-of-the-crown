package com.ashes.crown

fun main() {
    check(WorldManager.catalogue().size == 11)
    check(WorldManager.canonicalName("Порт Эстервала") == "Порт Веймар")
    check(WorldManager.location("Порт Эстервала")?.name == "Порт Веймар")
    check(WorldManager.normalizeLocation("Порт Эстервала") == "Порт Веймар")
    check(WorldManager.normalizeLocation("Несуществующий тракт") == "Королевский замок")
    check(WorldManager.normalizeLocation(null) == "Королевский замок")
    check(WorldManager.isUnlocked("Королевский замок", 0, 0))
    check(WorldManager.isUnlocked("Зимний сад", 0, 0))
    check(!WorldManager.isUnlocked("Старый мост", 4, 0))
    check(WorldManager.isUnlocked("Старый мост", 5, 0))
    check(!WorldManager.isUnlocked("Порт Веймар", 6, 0))
    check(WorldManager.isUnlocked("Порт Веймар", 7, 0))
    check(!WorldManager.isUnlocked("Старая часовня", 10, 0))
    check(WorldManager.isUnlocked("Старая часовня", 0, 1))

    val samePlace = WorldManager.planTravel("Королевский замок", "Королевский замок", 10, 0)
    check(samePlace is TravelPlan.Rejected)
    val locked = WorldManager.planTravel("Королевский замок", "Порт Веймар", 6, 0)
    check(locked is TravelPlan.Rejected)
    val unknown = WorldManager.planTravel("Королевский замок", "Неизвестная башня", 10, 0)
    check(unknown is TravelPlan.Rejected)
    val corruptOrigin = WorldManager.planTravel("Потерянная локация", "Зимний сад", 10, 0)
    check(corruptOrigin is TravelPlan.Rejected)

    val garden = WorldManager.planTravel("Королевский замок", "Зимний сад", 0, 0)
    check(garden is TravelPlan.Ready)
    garden as TravelPlan.Ready
    check(garden.hours == 1 && !garden.dangerousRoad)

    val forest = WorldManager.planTravel("Воронья деревня", "Лесной тракт", 5, 0, requestedHours = 2)
    check(forest is TravelPlan.Ready)
    forest as TravelPlan.Ready
    check(forest.hours == 2 && forest.dangerousRoad)

    val clamped = WorldManager.planTravel("Королевский замок", "Зимний сад", 0, 0, requestedHours = 99)
    check(clamped is TravelPlan.Ready && (clamped as TravelPlan.Ready).hours == 24)

    println("World catalogue, canonical names, unlock gates and travel-plan checks passed")
}
