package ru.vladimir.noctyss.event;

import org.bukkit.World;
import ru.vladimir.noctyss.api.EventAPI;
import ru.vladimir.noctyss.event.types.EventInstance;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.Set;

public class EventManager {

    public boolean startEvent(World world, EventType eventType, EventInstance eventInstance) {
        LoggerUtility.debug(this, "startEvent method has been called with parameters: %s, %s, %s"
                .formatted(world, eventType, eventInstance));

        if (EventAPI.startEvent(world, eventType, eventInstance)) {
            LoggerUtility.info(this, "Event '%s' successfully started in world '%s'"
                    .formatted(eventType.name(), world.getName()));
            return true;
        } else {
            LoggerUtility.info(this, "Failed to start event '%s' in world '%s'"
                    .formatted(eventType.name(), world.getName()));
            return false;
        }
    }

    public boolean stopEvent(World world, EventType eventType) {
        LoggerUtility.debug(this, "stopEvent method has been called with parameters: %s, %s"
                .formatted(world, eventType));

        if (EventAPI.stopEvent(world, eventType)) {
            LoggerUtility.info(this, "Event '%s' successfully stopped in '%s'"
                    .formatted(eventType.name(), world.getName()));
            return true;
        } else {
            LoggerUtility.info(this, "Failed to stop event '%s' in '%s'"
                    .formatted(eventType.name(), world.getName()));
            return false;
        }
    }

    public boolean stopAllEventsForWorld(World world) {
        LoggerUtility.info(this, "Stopping all events in world '%s'".formatted(world.getName()));

        boolean allEventsStopped = true;
        for (final EventType activeEvent : EventAPI.getActiveEventsInWorld(world)) {
            if (!stopEvent(world, activeEvent)) {
                allEventsStopped = false;
            }
        }

        if (allEventsStopped) {
            LoggerUtility.info(this, "All events were stopped successfully in world '%s'"
                    .formatted(world.getName()));
            return true;
        } else {
            LoggerUtility.info(this, "Some events failed to stop in world '%s'"
                    .formatted(world.getName()));
            return false;
        }
    }

    public void stopAllEvents() {
        final Set<World> worlds = EventAPI.getActiveEventsPerWorld().keySet();
        worlds.forEach(this::stopAllEventsForWorld);
    }
}
