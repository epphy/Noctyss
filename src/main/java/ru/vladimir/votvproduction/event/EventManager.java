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

    public void startEvent(EventType eventType, World world) {
        if (eventType == null || world == null) {
            LoggerUtility.warn(this, "Start error: Either event type or world is null: %s, %s".formatted(eventType, world));
            return;
        } else if (!EventAPI.hasWorldState(world) || EventAPI.hasActiveEvent(world, eventType) ||
                   !EventAPI.isEventAllowed(world, eventType)) {
            LoggerUtility.warn(this, "Cannot start event %s for world %s".formatted(eventType, world.getName()));
            return;
        }

        final EventInstance eventInstance = events.get(eventType).get();
        if (eventInstance == null) {
            LoggerUtility.warn(this, "Failed to start event because event instance occurred to be null");
            return;
        }

        eventInstance.start();
        if (!EventAPI.addEvent(world, eventType, eventInstance)) {
            LoggerUtility.info(this, "Event %s could not start for world %s".formatted(eventType, world.getName()));
            return;
        }
        LoggerUtility.info(this, "Event %s started for world %s".formatted(eventType, world.getName()));
    }

    public void stopEvent(EventType eventType, World world) {
        if (eventType == null || world == null) {
            LoggerUtility.warn(this, "Stop error: Either event type or world is null: %s, %s".formatted(eventType, world));
            return;
        } else if (!EventAPI.hasWorldState(world) || !EventAPI.hasActiveEvent(world, eventType)) {
            LoggerUtility.warn(this, "Stop error: Either world %s is not allowed or the event %s is not found"
                    .formatted(eventType, world.getName()));
            return;
        }

        final WorldState worldState = EventAPI.getWorldState(world);
        if (worldState == null) {
            LoggerUtility.warn(this, "Could not stop event %s for world %s because world state is null"
                    .formatted(eventType, world.getName()));
            return;
        }

        final EventInstance eventInstance = worldState.getActiveEvent(eventType);
        if (eventInstance == null) {
            LoggerUtility.warn(this, "Could not stop event %s for world %s because event instance is null"
                    .formatted(eventType, world.getName()));
            return;
        }

        eventInstance.stop();
        if (!worldState.removeEvent(eventType)) {
            LoggerUtility.info(this, "Could not stop event %s for world %s because event is not in the list"
                    .formatted(eventType, world.getName()));
            return;
        }
        LoggerUtility.info(this, "Stopped event %s in world %s".formatted(eventType, world.getName()));
    }

    public void stopAllEvents(World world) {
        if (world == null) {
            LoggerUtility.warn(this, "Stop error: World is null");
            return;
        }

        final WorldState worldState = EventAPI.getWorldState(world);
        if (worldState == null) {
            LoggerUtility.warn(this, "Could not stop all events for world %s because world state is null"
                    .formatted(world.getName()));
            return;
        }

        for (final EventType eventType : worldState.getActiveEventTypes()) {
            if (eventType == null) {
                LoggerUtility.warn(this, "Could not stop an event for world %s because event type is null"
                        .formatted(world.getName()));
                return;
            }
            stopEvent(eventType, world);
        }
    }

    private void registerEvents() {
        events.put(EventType.NIGHTMARE_NIGHT, NightmareNightInstance::new);
    }
}
