package ru.vladimir.noctyss.event.modules.notification.storage;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.*;

/**
 * A singleton service class responsible for managing notifications for players in different worlds
 * based on events and configurable rules. The class ensures consistency between the in-memory data
 * and the persistent storage, providing utilities for data retrieval and manipulation.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PlayerNotificationService {
    private static final String CLASS_NAME = PlayerNotificationService.class.getSimpleName();
    private static PlayerNotificationService instance;
    private PlayerNotificationStorage storage;
    private PlayerNotificationSerializer serializer;
    private Map<World, EnumMap<EventType, Map<String, Set<UUID>>>> data;

    /**
     * Retrieves the singleton instance of the class.
     * This method ensures that the class has been properly initialized before
     * returning the instance.
     *
     * @return the singleton instance of this class
     * @throws IllegalStateException if the class has not been initialized
     */
    public static PlayerNotificationService getInstance() {
        if (instance == null) {
            throw new IllegalStateException("%s has not been initialised yet".formatted(CLASS_NAME));
        }
        return instance;
    }

    /**
     * Initializes plugin's instance. Additionally, loads all its states
     * and loads data from the storage in the {@link #data} map.
     *
     * @param plugin plugin's instance to use to initialize some of the states.
     */
    public static void init(@NonNull JavaPlugin plugin) {
        if (instance == null) {
            instance = new PlayerNotificationService();
            instance.loadInstances(plugin);
            instance.updateData();
            LoggerUtility.info(CLASS_NAME, "initialised");
        } else {
            LoggerUtility.info(CLASS_NAME, "already initialized");
        }
    }

    /**
     * Loads all class' instances using the provided {@code JavaPlugin} instance.
     *
     * @param plugin plugin's instance to use to initialize some of the states.
     */
    private void loadInstances(@NotNull JavaPlugin plugin) {
        storage = new PlayerNotificationStorage(plugin);
        storage.save();
        serializer = new PlayerNotificationSerializer();
        data = new HashMap<>();
    }

    /**
     * Unloads the class by releasing its resources. If instance is
     * not initialised, no logic will be executed.
     */
    public static void unload() {
        if (instance == null) {
            LoggerUtility.info(CLASS_NAME, "Service was not initialized, skipping unload");
            return;
        }

        instance.updateStorage();
        instance.storage = null;
        instance.serializer = null;
        instance.data = null;
        instance = null;

        LoggerUtility.info(CLASS_NAME, "disabled");
    }

    /**
     * Updates the file storage using the {@link #data} map.
     *
     * @throws IllegalStateException if the class has not been initialized properly before invoking this method.
     */
    public void updateStorage() {
        checkInitialized();
        storage.store(serializer.serialize(data));
    }

    /**
     * Updates the {@link #data} map using the data from the storage file.
     *
     * @throws IllegalStateException if the class has not been properly initialized before calling this method.
     */
    public void updateData() {
        checkInitialized();
        data = serializer.deserialize(storage.retrieve());
    }

    /**
     * Retrieves the event data associated with a specific world. Initializes the data for the world
     * if it does not already exist.
     *
     * @param world the world for which the event data is being retrieved
     * @return a non-null map where the keys are event types and the values are maps containing event rules
     *         and the sets of associated player UUIDs
     * @throws IllegalStateException if the class has not been properly initialized
     */
    @NotNull
    private Map<EventType, Map<String, Set<UUID>>> getDataOfWorld(@NotNull World world) {
        checkInitialized();
        data.computeIfAbsent(world, newWorld -> new EnumMap<>(EventType.class));
        return data.get(world);
    }

    /**
     * Retrieves the map of event rules and their associated sets of player UUIDs for a specific event type
     * in the given world. If the event type data does not exist for the specified world, it is initialized.
     *
     * @param world the world for which the event data is being retrieved
     * @param eventType the specific event type for which the data is being accessed
     * @return a non-null map where the keys are rule identifiers and the values are sets of associated player UUIDs
     * @throws IllegalStateException if the class has not been properly initialized
     */
    @NotNull
    private Map<String, Set<UUID>> getDataOfWorldEvent(@NotNull World world, @NotNull EventType eventType) {
        Map<EventType, Map<String, Set<UUID>>> eventMap = getDataOfWorld(world);
        eventMap.computeIfAbsent(eventType, newEventType -> new HashMap<>());
        return eventMap.get(eventType);
    }

    /**
     * Retrieves the set of player UUIDs associated with a specific rule for a given world and event type.
     * If the data for the specified rule does not exist in the event type data, it is initialized to an empty set.
     *
     * @param world the world for which the rule data is being retrieved
     * @param eventType the specific event type for which the rule data is being accessed
     * @param rule the rule identifier specifying the context of the data
     * @return a non-null set of UUIDs representing the players associated with the specified rule
     * @throws IllegalStateException if the underlying data structure has not been properly initialized
     */
    @NotNull
    private Set<UUID> getDataOfWorldEventRule(@NotNull World world, @NotNull EventType eventType, @NotNull String rule) {
        Map<String, Set<UUID>> rulePlayerIds = getDataOfWorldEvent(world, eventType);
        rulePlayerIds.computeIfAbsent(rule, newRule -> new HashSet<>());
        return rulePlayerIds.get(rule);
    }

    /**
     * Retrieves the set of player UUIDs that are excluded based on the specified world, event type, and rule.
     * If no exclusion data is found for the given parameters, an empty set is returned.
     *
     * @param world the world in which the exclusions are applicable
     * @param eventType the type of the event for which exclusions are defined
     * @param rule the rule identifier specifying the context for the exclusions
     * @return an immutable, non-null set of UUIDs representing the excluded players
     */
    @NonNull
    public Set<UUID> getExcludedPlayerIdsFor(@NonNull World world, @NonNull EventType eventType, @NonNull String rule) {
        Map<String, Set<UUID>> rulePlayerIds = getDataOfWorldEvent(world, eventType);
        if (!rulePlayerIds.containsKey(rule)) return Set.of();
        return Set.copyOf(rulePlayerIds.get(rule));
    }

    /**
     * Adds new player IDs to the list of excluded player IDs for the given world, event type, and rule.
     * The changes are subsequently persisted by updating the storage.
     *
     * @param world the world associated with the event.
     * @param eventType the type of event to which this operation applies.
     * @param rule the rule identifier defining the context of the exclusion.
     * @param playerIds the set of player UUIDs to be excluded.
     */
    public void addNewExcludedPlayerIds(@NonNull World world, @NonNull EventType eventType, @NonNull String rule, @NonNull Set<UUID> playerIds) {
        Set<UUID> excludedPlayerIds = getDataOfWorldEventRule(world, eventType, rule);
        excludedPlayerIds.addAll(playerIds);
        updateStorage();
    }

    /**
     * Validates that all required components of the class have been properly initialized.
     *
     * @throws IllegalStateException if the class has not been fully initialized.
     */
    private void checkInitialized() {
        if (instance == null || storage == null || serializer == null || data == null) {
            throw new IllegalStateException("%s has not been initialized".formatted(CLASS_NAME));
        }
    }
}
