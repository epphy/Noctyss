package ru.vladimir.votvproduction.event;

import org.bukkit.World;
import ru.vladimir.votvproduction.api.EventAPI;
import ru.vladimir.votvproduction.event.types.EventInstance;
import ru.vladimir.votvproduction.utility.LoggerUtility;

public final class EventManager {
    public boolean startEvent(World world, EventType eventType, EventInstance eventInstance) {
        if (EventAPI.startEvent(world, eventType, eventInstance)) {
            LoggerUtility.info(this, "Event '%s' successfully started in world '%s'"
                    .formatted(eventType, world));
            return true;
        } else {
            LoggerUtility.warn(this, "Failed to start event '%s' in world '%s'"
                    .formatted(eventType, world));
            return false;
        }
    }

    public boolean stopEvent(World world, EventType eventType) {
        if (EventAPI.stopEvent(world, eventType)) {
            LoggerUtility.info(this, "Event '%s' successfully stopped in world '%s'"
                    .formatted(eventType, world));
            return true;
        } else {
            LoggerUtility.warn(this, "Failed to stop event '%s' in world '%s'"
                    .formatted(eventType, world));
            return false;
        }
    }

    public boolean stopAllEvents(World world) {
        LoggerUtility.info(this, "Stopping all events for world '%s'".formatted(world));

        boolean allEventsStopped = true;
        for (final EventType activeEvent : EventAPI.getActiveEventTypes(world)) {
            if (!stopEvent(world, activeEvent)) {
                allEventsStopped = false;
            }
        }

        if (allEventsStopped) {
            LoggerUtility.info(this, "All events were stopped successfully in world '%s'"
                    .formatted(world));
            return true;
        } else {
            LoggerUtility.info(this, "Some events failed to stop in world '%s'"
                    .formatted(world));
            return false;
        }
    }
}
