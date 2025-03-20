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
            LoggerUtility.info(EventAPI.class, "EventAPI initialised");
            return;
        }
        LoggerUtility.info(EventAPI.class, "EventAPI is already initialised");
    }

    /*

    WORLD STATE MANAGER SECTION

     */

    public static boolean isEventAllowed(World world, EventType eventType) {
        if (world == null || eventType == null) {
            LoggerUtility.warn(EventAPI.class, "World or event type is null: %s, %s".formatted(world, eventType));
            return false;
        }
        final WorldState worldState = worldStateManager.getWorldState(world);
        if (worldState == null) {
            return false;
        }
        return worldState.isEventAllowed(eventType);
    }

    public static boolean hasWorldState(World world) {
        if (world == null) {
            LoggerUtility.warn(EventAPI.class, "World is null");
            return false;
        }
        return worldStateManager.hasWorldState(world);
    }

    public static boolean hasActiveEvent(World world, EventType eventType) {
        if (world == null || eventType == null) {
            LoggerUtility.warn(EventAPI.class, "World or event type is null: %s, %s".formatted(world, eventType));
            return false;
        }

        final WorldState worldState = worldStateManager.getWorldState(world);
        if (worldState == null) {
            return false;
        }

        return worldState.isEventActive(eventType);
    }

    @Nullable
    public static WorldState getWorldState(World world) {
        return worldStateManager.getWorldState(world);
    }

    public static boolean addEvent(World world, EventType eventType, EventInstance eventInstance) {
        final WorldState worldState = worldStateManager.getWorldState(world);
        if (worldState == null) {
            return false;
        }

        return worldState.addEvent(eventType, eventInstance);
    }

    public static boolean removeEvent(World world, EventType eventType) {
        final WorldState worldState = worldStateManager.getWorldState(world);
        if (worldState == null) {
            return false;
        }

        return worldState.removeEvent(eventType);
    }
}
