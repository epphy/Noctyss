package ru.vladimir.noctyss.api;

import lombok.ToString;
import org.bukkit.World;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.event.types.EventInstance;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.List;

/**
 * The EventAPI class provides a centralized interface for managing events within
 * different worlds. It allows initialization, querying, starting, stopping,
 * and retrieving active events through the use of a {@code WorldStateManager}.
 * <p>
 * This class is final and cannot be instantiated. Events are managed at the
 * world level, with individual states tracked per {@code World} instance.
 */
@ToString
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

    public static boolean startEvent(World world, EventType eventType, EventInstance eventInstance) {
        LoggerUtility.debug("EventAPI", "startEvent method has been called called with parameters: %s, %s, %s"
                .formatted(world, eventType, eventInstance));

        if (world == null || eventType == null || eventInstance == null) {
            LoggerUtility.warn("EventAPI",
                    "Failed to add event. One or more arguments are null: World=%s, EventType=%s, EventInstance=%s"
                    .formatted(world, eventType, eventInstance));
            return false;
        }

        final WorldState worldState = worldStateManager.getWorldState(world);
        if (worldState.addActiveEvent(eventType, eventInstance)) {
            eventInstance.start();
            return true;
        }

        return false;
    }

    public static boolean stopEvent(World world, EventType eventType) {
        LoggerUtility.debug("EventAPI", "stopEvent method has been called called with parameters: %s, %s"
                .formatted(world, eventType));

        if (world == null || eventType == null) {
            LoggerUtility.warn("EventAPI",
                    "Failed to remove event. World or EventType is null: World=%s, EventType=%s"
                    .formatted(world, eventType));
            return false;
        }

        final WorldState worldState = worldStateManager.getWorldState(world);
        final EventInstance eventInstance = worldState.getActiveEvent(eventType);
        if (worldState.removeActiveEvent(eventType)) {
            eventInstance.stop();
            return true;
        }

        return false;
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

    public static List<EventType> getActiveEventTypes(World world) {
        return worldStateManager.getWorldState(world).getActiveEventTypes();
    }

    public static List<World> getWorldsWithAllowedEvent(EventType eventType) {
        return worldStateManager.getWorldsWithAllowedEvent(eventType);
    }
}
