package ru.vladimir.votvproduction.api;

import lombok.Getter;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;
import ru.vladimir.votvproduction.event.EventType;
import ru.vladimir.votvproduction.event.WorldState;
import ru.vladimir.votvproduction.event.WorldStateManager;
import ru.vladimir.votvproduction.event.types.EventInstance;
import ru.vladimir.votvproduction.utility.LoggerUtility;

@Getter
public class EventAPI {
    private static WorldStateManager worldStateManager;

    private EventAPI() {}

    public static void initialise(WorldStateManager worldStateManager) {
        if (EventAPI.worldStateManager == null) {
            EventAPI.worldStateManager = worldStateManager;
            LoggerUtility.info(EventAPI.class, "EventAPI initialised successfully");
            return;
        }
        LoggerUtility.info(EventAPI.class, "EventAPI is already initialized. Skipping reinitialization.");
    }

    // ================================
    // WORLD STATE MANAGER OPERATIONS
    // ================================


    public static boolean hasWorldState(World world) {
        if (world == null) {
            LoggerUtility.warn(EventAPI.class, "Failed to check world state because world is null.");
            return false;
        }
        return worldStateManager.hasWorldState(world);
    }

    public static boolean isEventAllowed(World world, EventType eventType) {
        if (world == null || eventType == null) {
            LoggerUtility.warn(EventAPI.class, "Failed to check if event is allowed. World or EventType is null: %s, %s."
                    .formatted(world, eventType));
            return false;
        }

        final WorldState worldState = worldStateManager.getWorldState(world);
        if (worldState == null) {
            LoggerUtility.warn(EventAPI.class, "Cannot check if event is allowed. WorldState is null for world: %s."
                    .formatted(world.getName()));
            return false;
        }

        return worldState.isEventAllowed(eventType);
    }

    public static boolean hasActiveEvent(World world, EventType eventType) {
        if (world == null || eventType == null) {
            LoggerUtility.warn(EventAPI.class, "Failed to check if active event exists. World or EventType is null: %s, %s."
                    .formatted(world, eventType));
            return false;
        }

        final WorldState worldState = worldStateManager.getWorldState(world);
        if (worldState == null) {
            LoggerUtility.warn(EventAPI.class, "Cannot check active events. WorldState is null for world: %s."
                    .formatted(world.getName()));
            return false;
        }

        return worldState.hasActiveEvent(eventType);
    }

    @Nullable
    public static WorldState getWorldState(World world) {
        if (world == null) {
            LoggerUtility.warn(EventAPI.class, "Failed to retrieve WorldState because the provided world is null.");
            return null;
        }

        return worldStateManager.getWorldState(world);
    }

    public static boolean addEvent(World world, EventType eventType, EventInstance eventInstance) {
        if (world == null || eventType == null || eventInstance == null) {
            LoggerUtility.warn(EventAPI.class,
                    "Failed to add event. One or more arguments are null: World=%s, EventType=%s, EventInstance=%s."
                            .formatted(world, eventType, eventInstance));
            return false;
        }

        final WorldState worldState = worldStateManager.getWorldState(world);
        if (worldState == null) {
            LoggerUtility.warn(EventAPI.class, "Cannot add event. WorldState is null for world: %s."
                    .formatted(world.getName()));
            return false;
        }

        return worldState.addEvent(eventType, eventInstance);
    }

    public static boolean removeEvent(World world, EventType eventType) {
        if (world == null || eventType == null) {
            LoggerUtility.warn(EventAPI.class, "Failed to remove event. World or EventType is null: %s, %s."
                    .formatted(world, eventType));
            return false;
        }

        final WorldState worldState = worldStateManager.getWorldState(world);
        if (worldState == null) {
            LoggerUtility.warn(EventAPI.class, "Cannot remove event. WorldState is null for world: %s."
                    .formatted(world.getName()));
            return false;
        }

        return worldState.removeEvent(eventType);
    }
}
