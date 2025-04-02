package ru.vladimir.noctyss.api;

import lombok.NonNull;
import org.bukkit.World;
import ru.vladimir.noctyss.event.EventType;

import java.util.*;

/**
 * The {@code WorldStateManager} class is responsible for managing the associations
 * of {@link World} instances with their corresponding {@link WorldState} objects.
 * This allows tracking and maintaining the states of different worlds in a structured
 * manner. The class provides functionalities to retrieve, list, and manage the states
 * of the worlds it oversees.
 * <p></p>
 * It's important to notice that this class is partially immutable. It means that
 * data can be changed internally through provided methods. However, users won't
 * see a change because all return data is copied and immutable.
 */
record WorldStateManager(Map<World, WorldState> worldStates) {

    // ================================
    // GETTERS
    // ================================

    /**
     * Retrieves the {@link WorldState} associated with the given {@link World}.
     * If the specified world does not have an associated {@link WorldState},
     * a new {@link WorldState} is created and returned.
     *
     * @param world the world for which to retrieve the state; must not be null
     * @return the {@code WorldState} associated with the given world
     */
    @NonNull
    WorldState getWorldState(@NonNull World world) {
        worldStates.computeIfAbsent(world, newWorld -> new WorldState(
                world.getUID(), new EnumMap<>(EventType.class), new EnumMap<>(EventType.class), new ArrayList<>()));
        return worldStates.get(world);
    }

    /**
     * Retrieves an unmodifiable list of all {@link World} instances currently
     * managed by this instance.
     *
     * @return An unmodifiable {@code List<World>} representing the worlds
     *         managed by this instance.
     */
    @NonNull
    List<World> getWorlds() {
        return List.copyOf(worldStates.keySet());
    }

    /**
     * Retrieves a list of all {@link WorldState} objects managed by this instance.
     *
     * @return An unmodifiable list of {@code WorldState} objects representing the current
     *         states of the worlds managed by this instance.
     */
    @NonNull
    List<WorldState> getWorldStates() {
        return List.copyOf(worldStates.values());
    }

    /**
     * Provides a set of entries representing the mapping between {@link World}
     * and their corresponding {@link WorldState} managed by this instance.
     * Each entry in the set consists of a {@code World} as the key and its
     * associated {@code WorldState} as the value.
     *
     * @return An unmodifiable {@code Set<Map.Entry<World, WorldState>>}
     *         representing the current entries in the mapping of worlds
     *         and their states.
     */
    @NonNull
    Set<Map.Entry<World, WorldState>> getWorldStatesEntries() {
        return Set.copyOf(worldStates.entrySet());
    }

    /**
     * Provides a view of the map containing the association between
     * worlds and their corresponding world states managed by this instance.
     *
     * @return An unmodifiable {@code Map<World, WorldState>} representing the current
     *         mapping of worlds to their states.
     */
    @Override @NonNull
    public Map<World, WorldState> worldStates() {
        return Map.copyOf(worldStates);
    }

    // ================================
    // OTHER
    // ================================

    @Override
    public String toString() {
        return "WorldStateManager{" +
                "worldStates=" + worldStates +
                '}';
    }
}
