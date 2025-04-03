package ru.vladimir.noctyss.api;

import lombok.experimental.UtilityClass;
import org.bukkit.World;
import ru.vladimir.noctyss.config.ConfigService;
import ru.vladimir.noctyss.event.EventType;

import java.util.*;

/**
 * A utility class that provides and manages the lifecycle of the singleton instance
 * of {@link WorldStateManager}. This class is responsible for initializing the
 * {@code WorldStateManager} based on the configuration retrieved from {@link ConfigService}.
 * It ensures centralized creation and management of {@link WorldStateManager} and its associated
 * {@link WorldState} for each {@link World}.
 */
@UtilityClass
class WorldStateManagerProvider {
    private WorldStateManager worldStateManager;

    /**
     * Provides an instance of {@link WorldStateManager}. If an instance already exists,
     * it returns the existing instance; otherwise, it initializes a new instance.
     * The initialization involves constructing {@link WorldState} objects for each
     * {@link World} based on the configuration retrieved from {@link ConfigService}.
     *
     * @return The singleton instance of {@link WorldStateManager} containing
     *         the mapping of {@link World} instances to their corresponding {@link WorldState}.
     */
    WorldStateManager provide() {
        if (worldStateManager != null) return worldStateManager;

        Map<World, WorldState> worldStates = new HashMap<>();
        Map<World, List<EventType>> worldAllowedEvents = ConfigService.getInstance().getGeneralConfig().getAllowedEventWorlds();

        for (Map.Entry<World, List<EventType>> entry : worldAllowedEvents.entrySet()) {

            World world = entry.getKey();
            UUID worldId = world.getUID();
            List<EventType> allowedEvents = entry.getValue();

            worldStates.put(world, new WorldState(
                    worldId, new EnumMap<>(EventType.class), new EnumMap<>(EventType.class), allowedEvents));
        }

        worldStateManager = new WorldStateManager(worldStates);
        return worldStateManager;
    }

    /**
     * Unloads the current instance of {@link WorldStateManager} by setting it to null.
     * This effectively clears any previously maintained state and ensures that the next
     * call to {@link #provide()} will reinitialize a new instance of {@link WorldStateManager}.
     */
    void unload() {
        worldStateManager = null;
    }
}
