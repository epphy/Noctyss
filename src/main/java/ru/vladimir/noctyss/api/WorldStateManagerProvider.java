package ru.vladimir.noctyss.api;

import org.bukkit.World;
import ru.vladimir.noctyss.config.ConfigService;
import ru.vladimir.noctyss.event.EventType;

import java.util.*;

public class WorldStateManagerProvider {

    WorldStateManager provide() {
        final Map<World, WorldState> worldStates = new HashMap<>();
        final Map<World, List<EventType>> worldAllowedEvents = ConfigService.getGeneralConfig().getAllowedEventWorlds();

        for (final Map.Entry<World, List<EventType>> entry : worldAllowedEvents.entrySet()) {

            final World world = entry.getKey();
            final UUID worldId = world.getUID();
            final List<EventType> allowedEvents = entry.getValue();

            worldStates.put(world, new WorldState(
                    worldId, new EnumMap<>(EventType.class), new EnumMap<>(EventType.class), allowedEvents));
        }

        return new WorldStateManager(worldStates);
    }
}
