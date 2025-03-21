package ru.vladimir.votvproduction.event;

import org.bukkit.World;
import ru.vladimir.votvproduction.api.EventAPI;
import ru.vladimir.votvproduction.event.types.EventInstance;
import ru.vladimir.votvproduction.event.types.nightmarenight.NightmareNightInstance;
import ru.vladimir.votvproduction.utility.LoggerUtility;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public class EventManager {
    private final Map<EventType, Supplier<EventInstance>> events = new EnumMap<>(EventType.class);

    public void initialise() {
        registerEvents();
    }

    public void startEvent(World world, EventType eventType) {
        if (world == null) {
            LoggerUtility.warn(this, "Cannot start event %s because World is null."
                    .formatted(eventType));
            return;
        } else if (eventType == null) {
            LoggerUtility.warn(this, "Cannot start event in World %s because EventType is null."
                    .formatted(world));
            return;
        } else if (!EventAPI.isEventAllowed(world, eventType)) {
            LoggerUtility.info(this, "Cannot start event. Event '%s' is not allowed in world '%s'."
                    .formatted(eventType, world.getName()));
            return;
        }

        final Supplier<EventInstance> eventSupplier = events.get(eventType);
        if (eventSupplier == null) {
            LoggerUtility.warn(this, "Cannot start event. No supplier is registered for event type '%s'."
                    .formatted(eventType));
            return;
        }

        final EventInstance eventInstance = eventSupplier.get();
        if (!EventAPI.addEvent(world, eventType, eventInstance)) {
            LoggerUtility.warn(this, "Failed to start event '%s' in world '%s'."
                    .formatted(eventType, world.getName()));
            return;
        }
        eventInstance.start();
        LoggerUtility.info(this, "Event '%s' successfully started in world '%s'."
                .formatted(eventType, world.getName()));
    }

    public boolean stopEvent(World world, EventType eventType) {
        if (world == null) {
            LoggerUtility.warn(this, "Cannot stop event %s because World is null."
                    .formatted(eventType));
            return false;
        } else if (eventType == null) {
            LoggerUtility.warn(this, "Cannot stop event in World %s because EventType is null."
                    .formatted(world));
            return false;
        }

        final WorldState worldState = EventAPI.getWorldState(world);
        if (worldState == null) {
            LoggerUtility.warn(this, "Cannot stop event %s in world %s because World State is null"
                    .formatted(eventType, world.getName()));
            return false;
        }

        final EventInstance eventInstance = worldState.getActiveEvent(eventType);
        if (eventInstance == null) {
            LoggerUtility.warn(this, "Cannot stop event %s in world %s because Event Instance is null"
                    .formatted(eventType, world.getName()));
            return false;
        }

        if (!worldState.removeEvent(eventType)) {
            LoggerUtility.info(this, "Cannot stop event %s for world %s because Event is not in the list"
                    .formatted(eventType, world.getName()));
            return false;
        }
        eventInstance.stop();
        LoggerUtility.info(this, "Stopped event %s in world %s"
                .formatted(eventType, world.getName()));
        return true;
    }

    public void stopAllEvents(World world) {
        LoggerUtility.info(this, "Stopping all events for world %s"
                .formatted(world));

        if (world == null) {
            LoggerUtility.warn(this, "Cannot stop all events because World is null.");
            return;
        }

        final WorldState worldState = EventAPI.getWorldState(world);
        if (worldState == null) {
            LoggerUtility.warn(this, "Cannot stop all events. No WorldState exists for world '%s'."
                    .formatted(world.getName()));
            return;
        }

        for (final EventType activeEvent : worldState.getActiveEventTypes()) {
            if (activeEvent == null) {
                LoggerUtility.warn(this, "Failed to stop active event in world '%s'."
                        .formatted(world.getName()));
                continue;
            } else if (!stopEvent(world, activeEvent)) {
                LoggerUtility.warn(this, "Failed to stop active event '%s' in world '%s'."
                        .formatted(activeEvent, world.getName()));
                continue;
            }
            LoggerUtility.info(this, "Stopped event '%s' in world '%s'"
                    .formatted(activeEvent, world.getName()));
        }
    }

    private void registerEvents() {
        for (final EventType eventType : EventType.values()) {
            events.put(eventType, eventType.getEventSupplier());
            LoggerUtility.info(this, "Event '%s' has been registered."
                    .formatted(eventType));
        }
        LoggerUtility.info(this, "All events have been registered successfully.");
    }
}
