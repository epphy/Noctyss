package ru.vladimir.noctyss.api;

import lombok.experimental.UtilityClass;
import org.bukkit.World;
import ru.vladimir.noctyss.config.ConfigService;
import ru.vladimir.noctyss.event.EventType;

import java.util.*;

@UtilityClass
class WorldStateManagerProvider {
    private static WorldStateManager worldStateManager;

    static WorldStateManager provide() {
        if (worldStateManager != null) return worldStateManager;

        Map<World, WorldState> worldStates = new HashMap<>();
        Map<World, List<EventType>> worldAllowedEvents = ConfigService.getGeneralConfig().getAllowedEventWorlds();

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
}
