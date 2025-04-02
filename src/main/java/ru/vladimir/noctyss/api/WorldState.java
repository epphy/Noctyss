package ru.vladimir.noctyss.api;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.event.types.EventInstance;
import ru.vladimir.noctyss.utility.GameTimeUtility;

import java.util.*;

/**
 * Represents the state of a world, encapsulating active events, last recorded event days,
 * and the list of allowed events specific to the world. Manages operations related
 * to event activity, permissions, and event last occurrence tracking.
 * <p>
 * This record uses the unique {@code worldId} to identify the specific world it pertains to.
 * It also provides mechanisms to interact with and modify event-related properties
 * such as adding or removing active or allowed events, and querying active or allowed
 * states of events.
 * <p>
 * To mention, the class is internally mutable, which means through methods the data
 * of arrays can be changed. However, it provides arrays' copies only, so the data will
 * not change for other users.
 */
record WorldState(UUID worldId, Map<EventType, EventInstance> activeEvents, Map<EventType, Long> lastDayEvents, List<EventType> allowedEvents) {

    // ================================
    // WORLD
    // ================================

    /**
     * Retrieves the {@code World} instance associated with the current {@code WorldState}.
     *
     * @return the non-null {@code World} instance corresponding to the current {@code worldId}.
     * @throws IllegalStateException if the associated {@code World} cannot be found.
     */
    @NonNull
    private World getWorld() {
        World world = Bukkit.getWorld(worldId);
        if (world == null)
            throw new IllegalStateException("World must not be null for: %s".formatted(this));
        return world;
    }

    // ================================
    // ACTIVE EVENTS
    // ================================

    /**
     * Adds an active event to the world state.
     *
     * @param eventType      the type of the event to be added.
     * @param eventInstance  the instance of the event to be associated with the event type.
     * @return {@code true} if the event was successfully added as an active event;
     *         {@code false} if the event is already active or not allowed.
     */
    boolean addActiveEvent(@NonNull EventType eventType, @NonNull EventInstance eventInstance) {
        if (isEventActive(eventType) || !isEventAllowed(eventType)) return false;
        activeEvents.put(eventType, eventInstance);
        return true;
    }

    /**
     * Removes an active event from the world state. Updates the last time
     * this exact event has occurred for this world.
     *
     * @param eventType the type of the event to be removed.
     * @return {@code true} if the event was successfully removed as an active event;
     *         {@code false} if the event was not active or not allowed.
     */
    boolean removeActiveEvent(@NonNull EventType eventType) {
        if (!isEventActive(eventType) || !isEventAllowed(eventType)) return false;
        updateEventLastDay(eventType, GameTimeUtility.getDay(getWorld()));
        activeEvents.remove(eventType);
        return true;
    }

    /**
     * Checks whether the specific event is active for this world.
     *
     * @param eventType the type of the event to check for activity.
     * @return {@code true} if the event is currently active;
     *         {@code false} otherwise.
     */
    boolean isEventActive(@NonNull EventType eventType) {
        return activeEvents.containsKey(eventType);
    }

    /**
     * Retrieves the currently active event instance for the specified event type.
     *
     * @param eventType the type of the event whose active instance is to be retrieved.
     * @return the active {@code EventInstance} associated with the specified event type.
     *         if event is not active, {@code null} will be returned instead.
     */
    @Nullable
    EventInstance getActiveEvent(@NonNull EventType eventType) {
        return activeEvents.get(eventType);
    }

    /**
     * Retrieves a list of event types that are currently active in the world state.
     *
     * @return a non-null, immutable list of {@code EventType} instances representing
     *         the active event types in the current world state.
     */
    @NonNull
    List<EventType> getActiveEventTypes() {
        return List.copyOf(activeEvents.keySet());
    }

    /**
     * Retrieves a list of all active event instances in the current world state.
     * The returned list is a non-null, immutable copy of the event instances
     * currently marked as active.
     *
     * @return a non-null, immutable list of {@code EventInstance} objects
     *         that represent the currently active events in the world.
     */
    @NonNull
    List<EventInstance> getActiveEventInstances() {
        return List.copyOf(activeEvents.values());
    }

    /**
     * Retrieves the active events in the world state as a set of map entries.
     * Each entry in the returned set represents a mapping between an {@code EventType}
     * and its corresponding {@code EventInstance}.
     *
     * @return a non-null, immutable set of {@code Map.Entry<EventType, EventInstance>}
     *         representing the currently active events in the world state.
     */
    @NonNull
    Set<Map.Entry<EventType, EventInstance>> getActiveEventsEntry() {
        return Set.copyOf(activeEvents.entrySet());
    }

    /**
     * Retrieves the currently active events in the world state.
     * The returned map represents the mapping between event types
     * and their corresponding active event instances.
     *
     * @return a non-null, immutable map where the keys are {@code EventType}
     *         instances representing the types of active events,
     *         and the values are {@code EventInstance} objects
     *         representing their respective active event instances.
     */
    @Override @NonNull
    public Map<EventType, EventInstance> activeEvents() {
        return Map.copyOf(activeEvents);
    }

    // ================================
    // LAST EVENTS
    // ================================

    /**
     * Updates the last occurrence day of a specific event type in the world state.
     *
     * @param eventType the type of the event whose last occurrence day is being updated.
     *                  Must not be null.
     * @param day       the day to be recorded as the last occurrence of the event type.
     */
    public void updateEventLastDay(@NonNull EventType eventType, long day) {
        lastDayEvents.put(eventType, day);
    }

    /**
     * Retrieves the last recorded day on which the specified event type occurred
     * in the world state. If the event type has no recorded occurrence, -1 is returned.
     *
     * @param eventType the type of the event whose last occurrence day is to be retrieved.
     *                  Must not be null.
     * @return the last recorded day of the event type if available;
     *         otherwise, -1 if the event type has no record.
     */
    long getEventLastDay(@NonNull EventType eventType) {
        return lastDayEvents.containsKey(eventType) ? lastDayEvents.get(eventType) : -1;
    }

    /**
     * Retrieves a list of the event types that last occurred in the past day.
     * The returned list is an immutable copy of the keys from the map
     * tracking last day's events and their occurrences in the world state.
     *
     * @return a non-null, immutable list of {@code EventType} instances representing
     *         the event types that had occurrences recorded in the last day.
     */
    @NonNull
    List<EventType> getLastDayEventTypes() {
        return List.copyOf(lastDayEvents.keySet());
    }

    /**
     * Retrieves a list of the last recorded days for event occurrences in the current world state.
     * The returned list is an immutable copy of the values from the map that tracks the last day
     * each event type occurred.
     *
     * @return a non-null, immutable list of {@code Long} values representing the last recorded
     *         days of event occurrences.
     */
    @NonNull
    List<Long> getLastDays() {
        return List.copyOf(lastDayEvents.values());
    }

    /**
     * Retrieves a set of entries representing the events and their corresponding
     * last recorded days in the current world state for the last day.
     * <p>
     * Each entry in the returned set contains an {@code EventType} as the key
     * and a {@code Long} value representing the last recorded day of the event.
     *
     * @return a non-null, immutable {@code Set} of {@code Map.Entry<EventType, Long>}
     *         representing the event types and their last recorded days for the last day.
     */
    @NonNull
    Set<Map.Entry<EventType, Long>> getLastDayEventEntries() {
        return Set.copyOf(lastDayEvents.entrySet());
    }

    /**
     * Retrieves a map of event types and their corresponding last recorded days
     * for events that occurred in the past day within the current world state.
     * <p>
     * The returned map is an immutable copy of the internal tracking map for
     * last day's events, where each key represents an {@code EventType} that
     * has occurred, and each value corresponds to the day on which it last occurred.
     *
     * @return a non-null, immutable map where the keys are {@code EventType}
     *         instances representing the types of events, and the values are
     *         {@code Long} values representing the last recorded days of those events.
     */
    @Override @NonNull
    public Map<EventType, Long> lastDayEvents() {
        return Map.copyOf(lastDayEvents);
    }

    // ================================
    // ALLOWED EVENTS
    // ================================

    /**
     * Adds the specified event type to the list of allowed events in the current world state.
     *
     * @param eventType the type of the event to be added to the allowed events list. Must not be null.
     * @return {@code true} if the event type was successfully added to the allowed events list;
     *         {@code false} if the event type was already present in the list.
     */
    boolean addAllowedEvent(@NonNull EventType eventType) {
        return allowedEvents.add(eventType);
    }

    /**
     * Removes the specified event type from the list of allowed events in the current world state.
     *
     * @param eventType the type of the event to be removed from the allowed events list. Must not be null.
     * @return {@code true} if the event type was successfully removed from the allowed events list;
     *         {@code false} if the event type was not present in the list.
     */
    boolean removeAllowedEvent(@NonNull EventType eventType) {
        return allowedEvents.remove(eventType);
    }

    /**
     * Determines if a given event type is allowed in the current world state.
     *
     * @param eventType the type of the event to check. Must not be null.
     * @return {@code true} if the event type is in the list of allowed events;
     *         {@code false} otherwise.
     */
    boolean isEventAllowed(@NonNull EventType eventType) {
        return allowedEvents.contains(eventType);
    }

    /**
     * Retrieves a list of event types that are allowed in the current world state.
     * The returned list is an immutable copy of the allowed events.
     *
     * @return a non-null, immutable list of {@code EventType} instances representing
     *         the event types that are allowed in the current world state.
     */
    @Override @NonNull
    public List<EventType> allowedEvents() {
        return List.copyOf(allowedEvents);
    }

    // ================================
    // OTHER
    // ================================

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        WorldState that = (WorldState) o;
        return Objects.equals(worldId, that.worldId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(worldId);
    }

    @Override
    public String toString() {
        return "WorldState{" +
                "worldId=" + worldId +
                ", activeEvents=" + activeEvents +
                ", lastDayEvents=" + lastDayEvents +
                ", allowedEvents=" + allowedEvents +
                '}';
    }
}
