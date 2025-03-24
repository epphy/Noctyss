package ru.vladimir.votvproduction.api;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.vladimir.votvproduction.event.EventType;
import ru.vladimir.votvproduction.event.types.EventInstance;

import java.util.List;
import java.util.Map;

public record WorldState(World world, Map<EventType, EventInstance> activeEvents, List<EventType> allowedEvents) {

    public boolean addEvent(EventType eventType, EventInstance eventInstance) {
        if (hasActiveEvent(eventType) || !isEventAllowed(eventType)) return false;
        activeEvents.put(eventType, eventInstance);
        return true;
    }

    public boolean removeEvent(EventType eventType) {
        if (!hasActiveEvent(eventType) || !isEventAllowed(eventType)) return false;
        activeEvents.remove(eventType);
        return true;
    }

    public boolean hasActiveEvent(EventType eventType) {
        return activeEvents.containsKey(eventType);
    }

    public boolean isEventAllowed(EventType eventType) {
        return allowedEvents.contains(eventType);
    }

    @Nullable
    public EventInstance getActiveEvent(EventType eventType) {
        return activeEvents.get(eventType);
    }

    @NotNull
    public List<EventType> getAllowedEvents() {
        return List.copyOf(allowedEvents);
    }

    @NotNull
    public List<EventType> getActiveEventTypes() {
        return List.copyOf(activeEvents.keySet());
    }

    @NotNull
    public List<EventInstance> getActiveEventInstances() {
        return List.copyOf(activeEvents.values());
    }

    @Override @NotNull
    public Map<EventType, EventInstance> activeEvents() {
        return Map.copyOf(activeEvents);
    }
}
