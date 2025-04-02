package ru.vladimir.noctyss.api;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.World;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.event.types.EventInstance;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.*;

/**
 * Utility class for managing and interacting with events across multiple worlds.
 * Provides methods to start, stop, query, and analyze events in the context of
 * their allowance and activity states. Works with a centralized {@code WorldStateManager}
 * for maintaining the state of each world in relation to events.
 * <p></p>
 * Note: This class uses a singleton-like design for initialization and assumes
 * that the {@code WorldStateManager} is provided during initialization.
 */
@UtilityClass
public class EventAPI {
    private static final String CLASS_NAME = "EventAPI";
    private static WorldStateManager worldStateManager;

    /**
     * Initializes the EventAPI by setting up the required {@code WorldStateManager}.
     * This method ensures that the necessary components are prepared for use in the system.
     * If the initialization has already been performed, it logs that the system is already initialized.
     * Otherwise, it creates and assigns a {@code WorldStateManager} instance using
     * {@code WorldStateManagerProvider.provide()} and marks the system as initialized.
     * <p>
     * If you try to use anything without initialising first, {code IllegalStateException} will be thrown.
     */
    public static void init() {
        if (worldStateManager == null) {
            worldStateManager = WorldStateManagerProvider.provide();
            LoggerUtility.info(CLASS_NAME, "initialised");
        } else {
            LoggerUtility.info(CLASS_NAME, "already initialized");
        }
    }

    // ================================
    // ACTIVE EVENTS
    // ================================

    /**
     * Starts an event of the specified type in the specified world using the provided event instance.
     *
     * @param world the world in which the event should be started, must not be null
     * @param eventType the type of event to be started, must not be null
     * @param eventInstance the instance of the event to be executed, must not be null
     * @return true if the event was successfully started, false otherwise
     */
    public static boolean startEvent(@NonNull World world, @NonNull EventType eventType, @NonNull EventInstance eventInstance) {
        checkInitialized();
        WorldState worldState = worldStateManager.getWorldState(world);
        if (!worldState.addActiveEvent(eventType, eventInstance)) {
            return false;
        }
        eventInstance.start();
        return true;
    }

    /**
     * Stops an active event of the specified type in the given world.
     *
     * @param world the world in which the event should be stopped
     * @param eventType the type of event to be stopped
     * @return true if the event was successfully stopped, false otherwise
     */
    public static boolean stopEvent(@NonNull World world, @NonNull EventType eventType) {
        checkInitialized();
        WorldState worldState = worldStateManager.getWorldState(world);
        EventInstance eventInstance = worldState.getActiveEvent(eventType);
        if (eventInstance == null || !worldState.removeActiveEvent(eventType)) {
            return false;
        }
        eventInstance.stop();
        return true;
    }

    /**
     * Determines whether a specific event is currently active in the given world.
     *
     * @param world the world in which to check if the event is active
     * @param eventType the type of event to check for activity
     * @return true if the specified event is currently active in the given world, false otherwise
     */
    public static boolean isEventActive(@NonNull World world, @NonNull EventType eventType) {
        checkInitialized();
        return worldStateManager.getWorldState(world).isEventActive(eventType);
    }

    /**
     * Retrieves a list of currently active event types in the specified world.
     *
     * @param world the world for which to retrieve the list of active events
     * @return a non-null, immutable list of {@code EventType} instances representing the active events in the specified world
     */
    @NonNull
    public static List<EventType> getActiveEventsInWorld(@NonNull World world) {
        checkInitialized();
        return worldStateManager.getWorldState(world).getActiveEventTypes();
    }

    /**
     * Retrieves a list of worlds where a specific event type is currently active.
     *
     * @param eventType the type of event to check for activity
     * @return a non-null, immutable list of {@code World} instances where the specified event is active
     */
    @NonNull
    public static List<World> getWorldsWithActiveEvent(@NonNull EventType eventType) {
        checkInitialized();
        List<World> result = new ArrayList<>();

        for (Map.Entry<World, WorldState> entry : worldStateManager.getWorldStatesEntries()) {
            if (!entry.getValue().isEventAllowed(eventType)) continue;
            if (entry.getValue().isEventActive(eventType)) result.add(entry.getKey());
        }

        return List.copyOf(result);
    }

    /**
     * Retrieves a list of worlds where a specific event type is currently inactive.
     *
     * @param eventType the type of event to check for inactivity, must not be null
     * @return a non-null, immutable list of {@code World} instances where the specified event type is inactive
     */
    @NonNull
    public static List<World> getWorldsWithoutEvent(@NonNull EventType eventType) {
        checkInitialized();
        List<World> result = new ArrayList<>();

        for (Map.Entry<World, WorldState> entry : worldStateManager.getWorldStatesEntries()) {
            if (!entry.getValue().isEventAllowed(eventType)) continue;
            if (!entry.getValue().isEventActive(eventType)) result.add(entry.getKey());
        }

        return List.copyOf(result);
    }

    /**
     * Retrieves a list of worlds that have any active event currently occurring.
     *
     * @return a non-null, immutable list of {@code World} instances where at least one event is active
     */
    @NonNull
    public static List<World> getWorldsWithAnyActiveEvent() {
        checkInitialized();
        List<World> result = new ArrayList<>();

        for (Map.Entry<World, WorldState> entry : worldStateManager.getWorldStatesEntries()) {
            if (!entry.getValue().getActiveEventTypes().isEmpty()) result.add(entry.getKey());
        }

        return List.copyOf(result);
    }

    /**
     * Retrieves a set of entries representing the association between worlds
     * and their respective active event types.
     *
     * @return a non-null, immutable set of {@code Map.Entry<World, List<EventType>>} where each entry maps a
     *         {@code World} to a list of its active {@code EventType}s
     */
    @NonNull
    public static Set<Map.Entry<World, List<EventType>>> getActiveEventsPerWorldEntries() {
        checkInitialized();
        return Set.copyOf(getActiveEventsPerWorld().entrySet());
    }

    /**
     * Retrieves a mapping between worlds and their respective active event types.
     * Each world is associated with a list of event types that are currently active within it.
     *
     * @return a non-null, immutable map where the key is the {@code World} instance and the value is a
     *         list of {@code EventType} instances representing the active events in that world.
     */
    @NonNull
    public static Map<World, List<EventType>> getActiveEventsPerWorld() {
        checkInitialized();
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

    /**
     * Determines if a specified event type is allowed in a given world.
     *
     * @param world the world in which to check if the event is allowed
     * @param eventType the type of event to check for permissions
     * @return {@code true} if the event type is allowed in the specified world; {@code false} otherwise
     */
    public static boolean isEventAllowed(@NonNull World world, @NonNull EventType eventType) {
        checkInitialized();
        return worldStateManager.getWorldState(world).isEventAllowed(eventType);
    }

    /**
     * Retrieves a list of event types that are allowed in the specified world.
     *
     * @param world the world for which to retrieve the allowed event types
     * @return a non-null, immutable list of {@code EventType} instances representing the allowed events in the specified world
     */
    @NonNull
    public static List<EventType> getAllowedEventsInWorld(@NonNull World world) {
        checkInitialized();
        return worldStateManager.getWorldState(world).allowedEvents();
    }

    /**
     * Retrieves a list of worlds where a specific event type is allowed.
     *
     * @param eventType the type of event to check for permission, must not be null
     * @return a non-null, immutable list of {@code World} instances where the specified event type is allowed
     */
    @NonNull
    public static List<World> getWorldsAllowingEvent(@NonNull EventType eventType) {
        checkInitialized();
        List<World> result = new ArrayList<>();

        for (Map.Entry<World, WorldState> entry : worldStateManager.getWorldStatesEntries()) {
            if (entry.getValue().isEventAllowed(eventType)) result.add(entry.getKey());
        }

        return List.copyOf(result);
    }

    /**
     * Retrieves a list of worlds where at least one event type is allowed.
     *
     * @return a non-null, immutable list of {@code World} instances where at least
     *         one event type is allowed.
     */
    @NonNull
    public static List<World> getWorldsWithAnyAllowedEvent() {
        checkInitialized();
        List<World> result = new ArrayList<>();

        for (Map.Entry<World, WorldState> entry : worldStateManager.getWorldStatesEntries()) {
            if (!entry.getValue().allowedEvents().isEmpty()) result.add(entry.getKey());
        }

        return List.copyOf(result);
    }

    /**
     * Retrieves a mapping between worlds and their respective allowed event types.
     * Each world is associated with a list of event types that are permitted in that world.
     *
     * @return a non-null, immutable map where the key is the {@code World} instance
     *         and the value is a list of {@code EventType} instances representing
     *         the allowed events in that specific world.
     */
    @NonNull
    public static Map<World, List<EventType>> getAllowedEventsPerWorld() {
        checkInitialized();
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

    /**
     * Retrieves the last recorded day on which the specified event type occurred in the given world.
     * If the event type has no recorded occurrence, the method returns -1.
     *
     * @param world the world in which the event's last occurrence day is being checked
     * @param eventType the type of event whose last occurrence day is to be retrieved
     * @return the last recorded day of the specified event type in the given world;
     *         returns -1 if the event type has no record in the specified world
     */
    public static long getLastDayTheEventWas(@NonNull World world, @NonNull EventType eventType) {
        return worldStateManager.getWorldState(world).getEventLastDay(eventType);
    }

    /**
     * Verifies that the EventAPI has been properly initialized before allowing
     * further operations. This method ensures that the essential components of the system
     * are initialized and ready to use.
     *
     * @throws IllegalStateException if the EventAPI has not been properly initialized.
     */
    private static void checkInitialized() {
        if (worldStateManager == null) {
            throw new IllegalStateException("EventAPI has not been initialized. Call init() first.");
        }
    }

}
