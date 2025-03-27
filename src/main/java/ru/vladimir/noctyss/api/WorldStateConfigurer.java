package ru.vladimir.noctyss.api;

import org.bukkit.World;
import ru.vladimir.noctyss.config.GeneralConfig;
import ru.vladimir.noctyss.event.EventType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record WorldStateConfigurer(GeneralConfig generalConfig) {

    WorldStateManager configure() {
        final Map<World, WorldState> worldStates = new HashMap<>();

        for (final Map.Entry<World, List<EventType>> entry : generalConfig.getAllowedEventWorlds().entrySet()) {

            final World world = entry.getKey();
            final List<EventType> allowedEvents = entry.getValue();

            worldStates.put(world, new WorldState(world, new HashMap<>(), new HashMap<>(), allowedEvents));
        }

        return new WorldStateManager(worldStates);
    }
}
