package ru.vladimir.noctyss.api;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.World;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.event.types.EventInstance;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.*;

@UtilityClass
public class EventAPI {
    private static final String CLASS_NAME = "EventAPI";
    private static WorldStateManager worldStateManager;

    public static void init(WorldStateManagerProvider worldStateManagerProvider) {
        if (worldStateManager == null) {
            worldStateManager = worldStateManagerProvider.provide();
            LoggerUtility.info(CLASS_NAME, "initialised");
        } else {
            LoggerUtility.info(CLASS_NAME, "already initialized");
        }
    }

    // ================================
    // ACTIVE EVENTS
    // ================================

    public static boolean startEvent(@NonNull World world, @NonNull EventType eventType, @NonNull EventInstance eventInstance) {
        WorldState worldState = worldStateManager.getWorldState(world);
        if (!worldState.addActiveEvent(eventType, eventInstance)) {
            return false;
        }
        eventInstance.start();
        return true;
    }

    public static boolean stopEvent(@NonNull World world, @NonNull EventType eventType) {
        WorldState worldState = worldStateManager.getWorldState(world);
        EventInstance eventInstance = worldState.getActiveEvent(eventType);
        if (eventInstance == null || !worldState.removeActiveEvent(eventType)) {
            return false;
        }
        eventInstance.stop();
        return true;
    }

    public static boolean isEventActive(@NonNull World world, @NonNull EventType eventType) {
        return worldStateManager.getWorldState(world).isEventActive(eventType);
    }

    @NonNull
    public static List<EventType> getActiveEventsInWorld(@NonNull World world) {
        return worldStateManager.getWorldState(world).allowedEvents();
    }

    @NonNull
    public static List<World> getWorldsWithSpecificActiveEvent(@NonNull EventType eventType) {
        List<World> result = new ArrayList<>();

        for (Map.Entry<World, WorldState> entry : worldStateManager.getWorldStatesEntries()) {
            if (entry.getValue().isEventActive(eventType)) result.add(entry.getKey());
        }

        return List.copyOf(result);
    }

    @NonNull
    public static List<World> getWorldsWithAnyActiveEvent() {
        List<World> result = new ArrayList<>();

        for (Map.Entry<World, WorldState> entry : worldStateManager.getWorldStatesEntries()) {
            if (!entry.getValue().getActiveEventTypes().isEmpty()) result.add(entry.getKey());
        }

        return List.copyOf(result);
    }

    @NonNull
    public static Map<World, List<EventType>> getActiveEventsPerWorld() {
        Map<World, List<EventType>> result = new HashMap<>();

        for (Map.Entry<World, WorldState> entry : worldStateManager.getWorldStatesEntries()) {
            WorldState worldState = entry.getValue();
            List<EventType> activeEventTypes = worldState.getActiveEventTypes();
            if (activeEventTypes.isEmpty()) continue;
            World world = entry.getKey();
            result.put(world, activeEventTypes);
        }

        return Map.copyOf(result);
    }

    // ================================
    // ALLOWED EVENTS
    // ================================

    public static boolean isEventAllowed(@NonNull World world, @NonNull EventType eventType) {
        return worldStateManager.getWorldState(world).isEventAllowed(eventType);
    }

    @NonNull
    public static List<EventType> getAllowedEventsInWorld(@NonNull World world) {
        return worldStateManager.getWorldState(world).allowedEvents();
    }

    @NonNull
    public static List<World> getWorldsWithSpecificAllowedEvent(@NonNull EventType eventType) {
        List<World> result = new ArrayList<>();

        for (Map.Entry<World, WorldState> entry : worldStateManager.getWorldStatesEntries()) {
            if (entry.getValue().isEventAllowed(eventType)) result.add(entry.getKey());
        }

        return List.copyOf(result);
    }

    @NonNull
    public static List<World> getWorldsWithAnyAllowedEvent() {
        List<World> result = new ArrayList<>();

        for (Map.Entry<World, WorldState> entry : worldStateManager.getWorldStatesEntries()) {
            if (!entry.getValue().allowedEvents().isEmpty()) result.add(entry.getKey());
        }

        return List.copyOf(result);
    }

    @NonNull
    public static Map<World, List<EventType>> getAllowedEventsPerWorld() {
        Map<World, List<EventType>> result = new HashMap<>();

        for (Map.Entry<World, WorldState> entry : worldStateManager.getWorldStatesEntries()) {
            WorldState worldState = entry.getValue();
            List<EventType> allowedEventTypes = worldState.allowedEvents();
            if (allowedEventTypes.isEmpty()) continue;
            World world = entry.getKey();
            result.put(world, allowedEventTypes);
        }

        return Map.copyOf(result);
    }

    // ================================
    // OTHER
    // ================================

    public static long getLastDayTheEventWas(@NonNull World world, @NonNull EventType eventType) {
        return worldStateManager.getWorldState(world).getEventLastDay(eventType);
    }
}
