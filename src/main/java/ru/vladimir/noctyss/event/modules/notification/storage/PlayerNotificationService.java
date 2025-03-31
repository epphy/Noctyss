package ru.vladimir.noctyss.event.modules.notification.storage;

import lombok.experimental.UtilityClass;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.*;

@UtilityClass
public class PlayerNotificationService {
    private static PlayerNotificationStorage storage;
    private static PlayerNotificationSerializer serializer;
    private static Map<World, EnumMap<EventType, Map<String, Set<UUID>>>> data = new HashMap<>();

    public static void init(JavaPlugin plugin) {
        if (storage == null || serializer == null) {
            loadInstances(plugin);
            storage.save();
            updateData();
            LoggerUtility.info("PlayerNotificationService", "initialised");
        } else {
            LoggerUtility.info("PlayerNotificationService", "Already initialised");
        }
    }

    private static void loadInstances(JavaPlugin plugin) {
        storage = new PlayerNotificationStorage(plugin);
        serializer = new PlayerNotificationSerializer();
    }

    public static void updateStorage() {
        storage.store(serializer.serialize(data));
    }

    public static void updateData() {
        data = serializer.deserialize(storage.retrieve());
    }

    @NotNull
    private static Map<EventType, Map<String, Set<UUID>>> getDataOfWorld(World world) {
        data.computeIfAbsent(world, newWorld -> new EnumMap<>(EventType.class));
        return data.get(world);
    }

    @NotNull
    private static Map<String, Set<UUID>> getDataOfWorldEvent(World world, EventType eventType) {
        final Map<EventType, Map<String, Set<UUID>>> eventMap = getDataOfWorld(world);
        eventMap.computeIfAbsent(eventType, newEventType -> new HashMap<>());
        return eventMap.get(eventType);
    }

    @NotNull
    private static Set<UUID> getDataOfWorldEventRule(World world, EventType eventType, String rule) {
        final Map<String, Set<UUID>> rulePlayerIds = getDataOfWorldEvent(world, eventType);
        rulePlayerIds.computeIfAbsent(rule, newRule -> new HashSet<>());
        return rulePlayerIds.get(rule);
    }

    @NotNull
    public static Set<UUID> getExcludedPlayersFor(World world, EventType eventType, String rule) {
        final Map<String, Set<UUID>> rulePlayerIds = getDataOfWorldEvent(world, eventType);
        if (!rulePlayerIds.containsKey(rule)) return Set.of();
        return Set.copyOf(rulePlayerIds.get(rule));
    }

    public static void addNewExcludedPlayerIds(World world, EventType eventType, String rule, Set<UUID> playerIds) {
        final Set<UUID> excludedPlayerIds = getDataOfWorldEventRule(world, eventType, rule);
        excludedPlayerIds.addAll(playerIds);
        updateStorage();
    }
}
