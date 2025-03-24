package ru.vladimir.votvproduction.api;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import ru.vladimir.votvproduction.event.EventType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the states of multiple {@code World} instances and provides access
 * to their respective {@code WorldState} objects. Each {@code WorldState}
 * encapsulates information about active and allowed events within a specific
 * {@code World}.
 * <p>
 * The {@code WorldStateManager} ensures that a {@code WorldState} exists for
 * a given {@code World}. If a requested {@code WorldState} does not exist, it
 * creates a new one with default configurations.
 */
record WorldStateManager(Map<World, WorldState> worldStates) {

    @NotNull
    public List<World> getWorldsWithAllowedEvent(EventType eventType) {
        final List<World> worlds = new ArrayList<>();
        for (final Map.Entry<World, WorldState> entry : worldStates().entrySet()) {
            if (entry.getValue().isEventAllowed(eventType)) {
                worlds.add(entry.getKey());
            }
        }
        return worlds;
    }

    @NotNull
    public List<WorldState> getWorldStatesWithAllowedEvent(EventType eventType) {
        final List<WorldState> worldStates = new ArrayList<>();
        for (final Map.Entry<World, WorldState> entry : worldStates().entrySet()) {
            final WorldState worldState = entry.getValue();
            if (worldState.isEventAllowed(eventType)) {
                worldStates.add(worldState);
            }
        }
        return worldStates;
    }

    @NotNull
    WorldState getWorldState(World world) {
        if (!hasWorldState(world)) {
            worldStates.put(world, new WorldState(world, new HashMap<>(), new ArrayList<>()));
        }
        return worldStates.get(world);
    }

    private boolean hasWorldState(World world) {
        return worldStates.containsKey(world);
    }

    @NotNull
    List<World> getWorlds() {
        return List.copyOf(worldStates.keySet());
    }

    @NotNull
    List<WorldState> getWorldStates() {
        return List.copyOf(worldStates.values());
    }

    @Override @NotNull
    public Map<World, WorldState> worldStates() {
        return Map.copyOf(worldStates);
    }

    @Override
    public String toString() {
        return "WorldStateManager{" +
                "worldStates=" + worldStates +
                '}';
    }
}
