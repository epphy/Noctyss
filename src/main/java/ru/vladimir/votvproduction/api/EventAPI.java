package ru.vladimir.votvproduction.api;

import org.bukkit.World;
import org.jetbrains.annotations.Nullable;
import ru.vladimir.votvproduction.event.EventType;
import ru.vladimir.votvproduction.event.types.EventInstance;
import ru.vladimir.votvproduction.utility.LoggerUtility;

public final class EventAPI {
    private static WorldStateManager worldStateManager;

    private EventAPI() {}

    public static void init(WorldStateManager worldStateManager) {
        if (EventAPI.worldStateManager == null) {
            EventAPI.worldStateManager = worldStateManager;
            LoggerUtility.info("EventAPI", "EventAPI has been initialised");
        } else {
            LoggerUtility.info("EventAPI", "EventAPI is already initialized");
        }
    }

    // ================================
    // WORLD STATE MANAGER OPERATIONS
    // ================================

    public static boolean hasWorldState(World world) {
        if (world == null) {
            LoggerUtility.warn("EventAPI", "Failed to check world state because world is null.");
            return false;
        }
        return worldStateManager.hasWorldState(world);
    }

    public static boolean isEventAllowed(World world, EventType eventType) {
        if (world == null || eventType == null) {
            LoggerUtility.warn("EventAPI", "Failed to check if event is allowed. World or EventType is null: %s, %s."
                    .formatted(world, eventType));
            return false;
        }

        final WorldState worldState = worldStateManager.getWorldState(world);
        if (worldState == null) {
            LoggerUtility.warn("EventAPI", "Cannot check if event is allowed. WorldState is null for world: %s."
                    .formatted(world.getName()));
            return false;
        }

        return worldState.isEventAllowed(eventType);
    }

    public static boolean hasActiveEvent(World world, EventType eventType) {
        if (world == null || eventType == null) {
            LoggerUtility.warn("EventAPI", "Failed to check if active event exists. World or EventType is null: %s, %s."
                    .formatted(world, eventType));
            return false;
        }

        final WorldState worldState = worldStateManager.getWorldState(world);
        if (worldState == null) {
            LoggerUtility.warn("EventAPI", "Cannot check active events. WorldState is null for world: %s."
                    .formatted(world.getName()));
            return false;
        }

        return worldState.isEventActive(eventType);
    }

    @Nullable
    public static WorldState getWorldState(World world) {
        if (world == null) {
            LoggerUtility.warn("EventAPI", "Failed to retrieve WorldState because the provided world is null.");
            return null;
        }

        return worldStateManager.getWorldState(world);
    }

    public static boolean addEvent(World world, EventType eventType, EventInstance eventInstance) {
        if (world == null || eventType == null || eventInstance == null) {
            LoggerUtility.warn("EventAPI",
                    "Failed to add event. One or more arguments are null: World=%s, EventType=%s, EventInstance=%s."
                            .formatted(world, eventType, eventInstance));
            return false;
        }

        final WorldState worldState = worldStateManager.getWorldState(world);
        if (worldState == null) {
            LoggerUtility.warn("EventAPI", "Cannot add event. WorldState is null for world: %s."
                    .formatted(world.getName()));
            return false;
        }

        return worldState.addActiveEvent(eventType, eventInstance);
    }

    public static boolean removeEvent(World world, EventType eventType) {
        if (world == null || eventType == null) {
            LoggerUtility.warn("EventAPI", "Failed to remove event. World or EventType is null: %s, %s."
                    .formatted(world, eventType));
            return false;
        }

        final WorldState worldState = worldStateManager.getWorldState(world);
        if (worldState == null) {
            LoggerUtility.warn("EventAPI", "Cannot remove event. WorldState is null for world: %s."
                    .formatted(world.getName()));
            return false;
        }

        return worldState.removeActiveEvent(eventType);
    }

    /*

    WE ARE FREE TO EXTENSION

     */
}
