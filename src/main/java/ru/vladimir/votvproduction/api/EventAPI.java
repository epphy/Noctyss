package ru.vladimir.votvproduction.api;

import org.bukkit.World;
import ru.vladimir.votvproduction.event.EventType;
import ru.vladimir.votvproduction.event.types.EventInstance;
import ru.vladimir.votvproduction.utility.LoggerUtility;

/**
 * A utility class for managing event-related operations within different worlds.
 * Provides functionality for initializing event systems, checking event permissions,
 * and managing active events in worlds.
 * <p>
 * This class is thread-safe and ensures that the underlying event management
 * subsystem is properly initialized before performing operations.
 * <p>
 * Note: This class cannot be instantiated as it is designed to operate as a
 * static utility class.
 */
public final class EventAPI {
    private static WorldStateManager worldStateManager;

    private EventAPI() {}

    public static void init(WorldStateConfigurer worldStateConfigurer) {
        if (EventAPI.worldStateManager == null) {
            worldStateManager = worldStateConfigurer.configure();
            LoggerUtility.info("EventAPI", "EventAPI has been initialised");
        } else {
            LoggerUtility.info("EventAPI", "EventAPI is already initialized");
        }
    }

    // ================================
    // WORLD STATE MANAGER OPERATIONS
    // ================================

    public static boolean isEventAllowed(World world, EventType eventType) {
        if (world == null || eventType == null) {
            LoggerUtility.warn("EventAPI",
                    "Failed to check if event is allowed. World or EventType is null: World=%s, EventType=%s"
                    .formatted(world, eventType));
            return false;
        }

        return worldStateManager.getWorldState(world).isEventAllowed(eventType);
    }

    public static boolean addActiveEvent(World world, EventType eventType, EventInstance eventInstance) {
        if (world == null || eventType == null || eventInstance == null) {
            LoggerUtility.warn("EventAPI",
                    "Failed to add event. One or more arguments are null: World=%s, EventType=%s, EventInstance=%s"
                    .formatted(world, eventType, eventInstance));
            return false;
        }

        return worldStateManager.getWorldState(world).addActiveEvent(eventType, eventInstance);
    }

    public static boolean removeActiveEvent(World world, EventType eventType) {
        if (world == null || eventType == null) {
            LoggerUtility.warn("EventAPI",
                    "Failed to remove event. World or EventType is null: World=%s, EventType=%s"
                    .formatted(world, eventType));
            return false;
        }

        return worldStateManager.getWorldState(world).removeActiveEvent(eventType);
    }

    public static boolean isEventActive(World world, EventType eventType) {
        if (world == null || eventType == null) {
            LoggerUtility.warn("EventAPI",
                    "Failed to check if active event exists. World or EventType is null: World=%s, EventType=%s"
                    .formatted(world, eventType));
            return false;
        }

        return worldStateManager.getWorldState(world).isEventActive(eventType);
    }
}
