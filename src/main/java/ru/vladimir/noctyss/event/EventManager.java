package ru.vladimir.noctyss.event;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.World;
import ru.vladimir.noctyss.api.EventAPI;
import ru.vladimir.noctyss.event.types.EventInstance;
import ru.vladimir.noctyss.utility.LoggerUtility;

/**
 * A utility class for managing events across different worlds. This class
 * provides methods to start, stop specific events, stop all events in a
 * particular world, and stop all events globally.
 */
@UtilityClass
public class EventManager {
    private final String CLASS_NAME = EventManager.class.getSimpleName();

    /**
     * Starts a specific {@code event} in a specific {@code world} using a provided {@code instance}.
     *
     * @param world         the world to start an event in
     * @param eventType     the event to start
     * @param eventInstance the event instance which will be used
     * @return whether the operation has succeeded
     */
    public boolean startEvent(@NonNull World world, @NonNull EventType eventType, @NonNull EventInstance eventInstance) {
        if (EventAPI.startEvent(world, eventType, eventInstance)) {
            LoggerUtility.info(CLASS_NAME, "Event %s started in world: %s".formatted(eventType.name(), world.getName()));
            return true;
        }

        LoggerUtility.info(CLASS_NAME, "Failed to start event %s in world: %s".formatted(eventType.name(), world.getName()));
        return false;
    }

    /**
     * Stops a specific {@code event} in a specific {@code world}.
     *
     * @param world     the world to stop an event in
     * @param eventType the event to stop
     * @return whether the operation has succeeded
     */
    public boolean stopEvent(@NonNull World world, @NonNull EventType eventType) {
        if (EventAPI.stopEvent(world, eventType)) {
            LoggerUtility.info(CLASS_NAME, "Event %s stopped in: %s".formatted(eventType.name(), world.getName()));
            return true;
        }

        LoggerUtility.info(CLASS_NAME, "Failed to stop event '%s' in '%s'".formatted(eventType.name(), world.getName()));
        return false;
    }

    /**
     * Stops all events in a specific {@code world}.
     *
     * @param world the world to stop all events in
     * @return whether the operation has succeeded
     */
    public boolean stopAllEventsInWorld(@NonNull World world) {
        final boolean allEventsStopped = EventAPI.getActiveEventsInWorld(world).stream()
                .allMatch(activeEvent -> stopEvent(world, activeEvent));

        if (allEventsStopped) {
            LoggerUtility.info(CLASS_NAME, "Stopped all events in world: %s".formatted(world.getName()));
            return true;
        }

        LoggerUtility.info(CLASS_NAME, "Some events failed to stop in world: %s".formatted(world.getName()));
        return false;
    }

    /**
     * Stops all events in all worlds.
     *
     * @return whether the operation has succeeded
     */
    public boolean stopAllEvents() {
        EventAPI.getActiveEventsPerWorld().keySet().forEach(EventManager::stopAllEventsInWorld);
        return true;
    }
}
