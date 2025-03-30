package ru.vladimir.noctyss.api;

import org.bukkit.World;
import ru.vladimir.noctyss.config.ConfigService;
import ru.vladimir.noctyss.event.EventType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldStateConfigurer {
    WorldStateManager configure() {
        final Map<World, WorldState> worldStates = new HashMap<>();
        final Map<World, List<EventType>> worldAllowedEvents = ConfigService.getGeneralConfig().getAllowedEventWorlds();

        for (final Map.Entry<World, List<EventType>> entry : worldAllowedEvents.entrySet()) {

            final World world = entry.getKey();
            final List<EventType> allowedEvents = entry.getValue();

            worldStates.put(world, new WorldState(world, new HashMap<>(), new HashMap<>(), allowedEvents));
        }

        return new WorldStateManager(worldStates);
    }
}
