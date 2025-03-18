package ru.vladimir.votvproduction.event;

import org.bukkit.World;
import org.jetbrains.annotations.Nullable;
import ru.vladimir.votvproduction.event.events.EventInstance;
import ru.vladimir.votvproduction.event.events.EventType;
import ru.vladimir.votvproduction.utility.LoggerUtility;

import java.util.List;
import java.util.Map;

public record WorldState(World world, Map<EventType, EventInstance> activeEvents) {

    public boolean isEventActive(EventType eventType) {
        return activeEvents.containsKey(eventType);
    }

    public List<EventType> getActiveEventTypes() {
        return List.copyOf(activeEvents.keySet());
    }

    @Nullable
    public EventInstance getActiveEvent(EventType eventType) {
        if (!isEventActive(eventType)) {
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
}
