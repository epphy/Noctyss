package ru.vladimir.votvproduction.event;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public record WorldStateManager(Map<World, WorldState> worldStates) {

    public boolean hasWorldState(World world) {
        return worldStates.containsKey(world);
    }

    @Nullable
    public WorldState getWorldState(World world) {
        return worldStates.get(world);
    }

    @NotNull
    public List<World> getWorlds() {
        return List.copyOf(worldStates.keySet());
    }

    @NotNull
    public List<WorldState> getWorldStates() {
        return List.copyOf(worldStates.values());
    }

    @Override @NotNull
    public Map<World, WorldState> worldStates() {
        return Map.copyOf(worldStates);
    }
}
