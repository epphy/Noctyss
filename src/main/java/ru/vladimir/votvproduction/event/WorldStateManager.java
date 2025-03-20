package ru.vladimir.votvproduction.event;

import org.bukkit.World;
import org.jetbrains.annotations.Nullable;
import ru.vladimir.votvproduction.utility.LoggerUtility;

import java.util.List;
import java.util.Map;

public record WorldStateManager(Map<World, WorldState> worldStates) {

    public boolean hasWorldState(World world) {
        return worldStates.containsKey(world);
    }

    public List<World> getWorlds() {
        return List.copyOf(worldStates.keySet());
    }

    @Nullable
    public WorldState getWorldState(World world) {
        if (!hasWorldState(world)) {
            return null;
        }
        return worldStates.get(world);
    }

    public List<WorldState> getWorldStates() {
        return List.copyOf(worldStates.values());
    }

    @Override
    public Map<World, WorldState> worldStates() {
        return Map.copyOf(worldStates);
    }
}
