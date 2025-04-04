package ru.vladimir.noctyss.api;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.World;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.event.types.EventInstance;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.*;

/**
 * A utility class that provides methods for managing events in various worlds. This includes functionalities
 * for starting, stopping, and querying events as well as determining the allowed events in specific worlds.
 * <p>
 * All methods in this class operate on the global state of events, fetching and modifying information from
 * the {@code WorldStateManager}. The class also handles the association of events with their respective worlds.
 * <p>
 * The class is dependent on {@link WorldStateManagerProvider} which provides the access to
 * the {@link WorldStateManager} instance already.
 */
@UtilityClass
public class EventAPI {
    private final String CLASS_NAME = EventAPI.class.getSimpleName();

    /**
     * Unloads the current state of the EventAPI by clearing its {@code WorldStateManager}.
     * This method is used to release resources and reset the system state associated
     * with the {@code WorldStateManager}.
     * <p>
     * If the {@code WorldStateManager} is not null, it is set to {@code null}, and a log entry
     * indicating that the EventAPI has been unloaded is recorded.
     */
    public void unload() {
        WorldStateManagerProvider.unload();
        LoggerUtility.info(CLASS_NAME, "unloaded");
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
    public boolean startEvent(@NonNull World world, @NonNull EventType eventType, @NonNull EventInstance eventInstance) {
        WorldState worldState = WorldStateManagerProvider.provide().getWorldState(world);
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
    public boolean stopEvent(@NonNull World world, @NonNull EventType eventType) {
        WorldState worldState = WorldStateManagerProvider.provide().getWorldState(world);
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
    public boolean isEventActive(@NonNull World world, @NonNull EventType eventType) {
        return WorldStateManagerProvider.provide().getWorldState(world).isEventActive(eventType);
    }

    public boolean isAnyEventActive(@NonNull World world) {
        return !WorldStateManagerProvider.provide().getWorldState(world).activeEvents().isEmpty();
    }

    /**
     * Retrieves a list of currently active event types in the specified world.
     *
     * @param world the world for which to retrieve the list of active events
     * @return a non-null, immutable list of {@code EventType} instances representing the active events in the specified world
     */
    @NonNull
    public List<EventType> getActiveEventsInWorld(@NonNull World world) {
        return WorldStateManagerProvider.provide().getWorldState(world).getActiveEventTypes();
    }

    /**
     * Retrieves a list of worlds where a specific event type is currently active.
     *
     * @param eventType the type of event to check for activity
     * @return a non-null, immutable list of {@code World} instances where the specified event is active
     */
    @NonNull
    public List<World> getWorldsWithActiveEvent(@NonNull EventType eventType) {
        List<World> result = new ArrayList<>();

        for (Map.Entry<World, WorldState> entry : WorldStateManagerProvider.provide().getWorldStatesEntries()) {
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
    public List<World> getWorldsWithoutEvent(@NonNull EventType eventType) {
        List<World> result = new ArrayList<>();

        for (Map.Entry<World, WorldState> entry : WorldStateManagerProvider.provide().getWorldStatesEntries()) {
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
    public List<World> getWorldsWithAnyActiveEvent() {
        List<World> result = new ArrayList<>();

        for (Map.Entry<World, WorldState> entry : WorldStateManagerProvider.provide().getWorldStatesEntries()) {
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
    public Set<Map.Entry<World, List<EventType>>> getActiveEventsPerWorldEntries() {
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
    public Map<World, List<EventType>> getActiveEventsPerWorld() {
        Map<World, List<EventType>> result = new HashMap<>();

        for (Map.Entry<World, WorldState> entry : WorldStateManagerProvider.provide().getWorldStatesEntries()) {
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
    public boolean isEventAllowed(@NonNull World world, @NonNull EventType eventType) {
        return WorldStateManagerProvider.provide().getWorldState(world).isEventAllowed(eventType);
    }

    /**
     * Retrieves a list of event types that are allowed in the specified world.
     *
     * @param world the world for which to retrieve the allowed event types
     * @return a non-null, immutable list of {@code EventType} instances representing the allowed events in the specified world
     */
    @NonNull
    public List<EventType> getAllowedEventsInWorld(@NonNull World world) {
        return WorldStateManagerProvider.provide().getWorldState(world).allowedEvents();
    }

    /**
     * Retrieves a list of worlds where a specific event type is allowed.
     *
     * @param eventType the type of event to check for permission, must not be null
     * @return a non-null, immutable list of {@code World} instances where the specified event type is allowed
     */
    @NonNull
    public List<World> getWorldsAllowingEvent(@NonNull EventType eventType) {
        List<World> result = new ArrayList<>();

        for (Map.Entry<World, WorldState> entry : WorldStateManagerProvider.provide().getWorldStatesEntries()) {
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
    public List<World> getWorldsWithAnyAllowedEvent() {
        List<World> result = new ArrayList<>();

        for (Map.Entry<World, WorldState> entry : WorldStateManagerProvider.provide().getWorldStatesEntries()) {
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
    public Map<World, List<EventType>> getAllowedEventsPerWorld() {
        Map<World, List<EventType>> result = new HashMap<>();

        for (Map.Entry<World, WorldState> entry : WorldStateManagerProvider.provide().getWorldStatesEntries()) {
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
    public long getLastDayTheEventWas(@NonNull World world, @NonNull EventType eventType) {
        return WorldStateManagerProvider.provide().getWorldState(world).getEventLastDay(eventType);
    }
}
