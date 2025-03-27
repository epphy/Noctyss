package ru.vladimir.noctyss.api;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.event.types.EventInstance;

import java.util.List;
import java.util.Map;

/**
 * Represents the state of a world, encompassing active events and allowed events.
 * Provides mechanisms to manage and query events specific to a world.
 * <p>
 * The {@code WorldState} record consists of:
 * - The associated {@code World} instance.
 * - A mapping of currently active events and their corresponding {@code EventInstance}.
 * - A list of permitted events that can be activated within the world.
 * <p>
 * Notable functions include adding or removing active events, managing the list of allowed events,
 * and querying whether a specific event is currently active or allowed.
 * <p>
 * This record is immutable, but the map of active events and the list of allowed events are encapsulated
 * through accessor methods for thread-safe interaction.
 *
 * @param world           The world instance this state corresponds to.
 * @param activeEvents    A map tracking currently active events by their {@code EventType}.
 * @param allowedEvents   A list of {@code EventType} that can be triggered in the world.
 */
record WorldState(World world, Map<EventType, EventInstance> activeEvents, List<EventType> allowedEvents) {

    // ================================
    // ACTIVE EVENTS
    // ================================

    boolean addActiveEvent(EventType eventType, EventInstance eventInstance) {
        if (isEventActive(eventType) || !isEventAllowed(eventType)) return false;
        activeEvents.put(eventType, eventInstance);
        return true;
    }

    boolean removeActiveEvent(EventType eventType) {
        if (!isEventActive(eventType) || !isEventAllowed(eventType)) return false;
        activeEvents.remove(eventType);
        return true;
    }

    boolean isEventActive(EventType eventType) {
        return activeEvents.containsKey(eventType);
    }

    @NotNull
    EventInstance getActiveEvent(EventType eventType) {
        return activeEvents.get(eventType);
    }

    @NotNull
    List<EventType> getActiveEventTypes() {
        return List.copyOf(activeEvents.keySet());
    }

    @NotNull
    List<EventInstance> getActiveEventInstances() {
        return List.copyOf(activeEvents.values());
    }

    @Override @NotNull
    public Map<EventType, EventInstance> activeEvents() {
        return Map.copyOf(activeEvents);
    }

    // ================================
    // ALLOWED EVENTS
    // ================================

    boolean addAllowedEvent(EventType eventType) {
        return allowedEvents.add(eventType);
    }

    boolean removeAllowedEvent(EventType eventType) {
        return allowedEvents.remove(eventType);
    }

    boolean isEventAllowed(EventType eventType) {
        return allowedEvents.contains(eventType);
    }

    @NotNull
    List<EventType> getAllowedEvents() {
        return List.copyOf(allowedEvents);
    }

    @Override
    public String toString() {
        return "WorldState{" +
                "world=" + world +
                ", activeEvents=" + activeEvents +
                ", allowedEvents=" + allowedEvents +
                '}';
    }
}
