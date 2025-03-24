package ru.vladimir.votvproduction.event;

import org.bukkit.World;
import ru.vladimir.votvproduction.api.EventAPI;
import ru.vladimir.votvproduction.event.types.EventInstance;
import ru.vladimir.votvproduction.utility.LoggerUtility;

import java.util.ArrayList;
import java.util.List;

public final class EventManager {
    private static final List<EventType> EVENTS = new ArrayList<>();

    public void initialise() {
        registerEvents();
    }

    public boolean startEvent(World world, EventType eventType, EventInstance eventInstance) {
        if (!EventAPI.isEventAllowed(world, eventType)) {
            LoggerUtility.info(this, "Cannot start event. Event '%s' is not allowed in world '%s'."
                    .formatted(eventType, world));
            return false;
        }

        if (EventAPI.addActiveEvent(world, eventType, eventInstance)) {
            eventInstance.start();
            LoggerUtility.info(this, "Event '%s' successfully started in world '%s'."
                    .formatted(eventType, world));
            return true;
        } else {
            LoggerUtility.warn(this, "Failed to start event '%s' in world '%s'."
                    .formatted(eventType, world));
            return false;
        }
    }

    public boolean stopEvent(World world, EventType eventType) {
        final WorldState worldState = EventAPI.getWorldState(world);
        if (worldState == null) {
            LoggerUtility.warn(this, "Cannot stop event '%s' in world '%s' because WorldState is null."
                    .formatted(eventType, world));
            return false;
        }

        final EventInstance eventInstance = worldState.getActiveEvent(eventType);
        if (eventInstance == null) {
            LoggerUtility.warn(this, "Cannot stop event '%s' in world '%s' because EventInstance is null."
                    .formatted(eventType, world));
            return false;
        }

        if (EventAPI.removeActiveEvent(world, eventType)) {
            eventInstance.stop();
            LoggerUtility.info(this, "Event '%s' successfully stopped in world '%s'.".formatted(eventType, world));
            return true;
        } else {
            LoggerUtility.info(this, "Failed to stop event '%s' in world '%s'.".formatted(eventType, world));
            return false;
        }
    }

    public boolean stopAllEvents(World world) {
        LoggerUtility.info(this, "Stopping all events for world '%s'.".formatted(world));

        final WorldState worldState = EventAPI.getWorldState(world);
        if (worldState == null) {
            LoggerUtility.warn(this, "Cannot stop all events. No WorldState exists for world '%s'."
                    .formatted(world));
            return false;
        }

        boolean allEventsStopped = true;
        for (final EventType activeEvent : worldState.getActiveEventTypes()) {
            if (!stopEvent(world, activeEvent)) {
                allEventsStopped = false;
            }
        }

        if (allEventsStopped) {
            LoggerUtility.info(this, "All events were stopped successfully in world '%s'.".formatted(world));
        } else {
            LoggerUtility.info(this, "Some events failed to stop in world '%s'.".formatted(world));
        }
        return true;
    }

    private void registerEvents() {


        LoggerUtility.info(this, "All events have been registered successfully.");
    }
}
