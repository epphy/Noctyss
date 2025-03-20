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
        }
    }

    public void stopAllEvents(World world) {
        if (world == null) {
            LoggerUtility.warn(this, "Stop error: World is null");
            return;
        }
    }

    private void registerEvents() {
        events.put(EventType.NIGHTMARE_NIGHT, NightmareNightInstance::new);
    }
}
