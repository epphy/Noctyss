package ru.vladimir.votvproduction.event;

import org.bukkit.World;
import org.jetbrains.annotations.Nullable;
import ru.vladimir.votvproduction.event.types.EventInstance;
import ru.vladimir.votvproduction.utility.LoggerUtility;

import java.util.List;
import java.util.Map;

public record WorldState(World world, Map<EventType, EventInstance> activeEvents, List<EventType> allowedEvents) {

    public boolean addEvent(EventType eventType, EventInstance eventInstance) {
        if (hasActiveEvent(eventType) || !isEventAllowed(eventType)) return false;
        activeEvents.put(eventType, eventInstance);
        return true;
    }

    public boolean removeEvent(EventType eventType) {
        if (!hasActiveEvent(eventType)) return false;
        activeEvents.remove(eventType);
        return true;
    }

    public boolean hasActiveEvent(EventType eventType) {
        return activeEvents.containsKey(eventType);
    }

    public List<EventType> getActiveEventTypes() {
        return List.copyOf(activeEvents.keySet());
    }

    @Nullable
    public EventInstance getActiveEvent(EventType eventType) {
        if (!hasActiveEvent(eventType)) {
            LoggerUtility.warn(this.getClass(), "Event %s is not present in the map of world %s".formatted(eventType, world.getName()));
            return null;
        }
        return activeEvents.get(eventType);
    }

    public List<EventInstance> getActiveEventInstances() {
        return List.copyOf(activeEvents.values());
    }

    @Override
    public Map<EventType, EventInstance> activeEvents() {
        return Map.copyOf(activeEvents);
    }

    public List<EventType> getAllowedEvents() {
        return List.copyOf(allowedEvents);
    }

    public boolean isEventAllowed(EventType eventType) {
        return allowedEvents.contains(eventType);
    }
}
