package ru.vladimir.votvproduction.api;

import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import ru.vladimir.votvproduction.config.GeneralConfig;
import ru.vladimir.votvproduction.event.EventType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class WorldStateConfigurer {
    private final GeneralConfig generalConfig;

    WorldStateManager configure() {
        final Map<World, WorldState> worldStates = new HashMap<>();

        for (final Map.Entry<World, List<EventType>> entry : generalConfig.getAllowedEventWorlds().entrySet()) {

            final World world = entry.getKey();
            final List<EventType> allowedEvents = entry.getValue();

            worldStates.put(world, new WorldState(world, new HashMap<>(), allowedEvents));
        }

        return new WorldStateManager(worldStates);
    }
}
