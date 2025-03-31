package ru.vladimir.noctyss.event.modules.notification.storage;

import org.bukkit.Bukkit;
import org.bukkit.World;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.*;
import java.util.stream.Collectors;

class PlayerNotificationSerializer {

    Map<String, Map<String, Map<String, Set<String>>>> serialize(Map<World, EnumMap<EventType, Map<String, Set<UUID>>>> data) {
        return data.entrySet().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getName(), // World name as key
                        entry -> entry.getValue().entrySet().stream()
                                .filter(Objects::nonNull)
                                .collect(Collectors.toMap(
                                        eventEntry -> eventEntry.getKey().name(), // EventType name
                                        eventEntry -> eventEntry.getValue().entrySet().stream()
                                                .filter(Objects::nonNull)
                                                .collect(Collectors.toMap(
                                                        Map.Entry::getKey, // Rule name
                                                        ruleEntry -> ruleEntry.getValue().stream()
                                                                .filter(Objects::nonNull)
                                                                .map(UUID::toString) // Convert UUIDs to strings
                                                                .collect(Collectors.toSet())
                                                ))
                                ))
                ));
    }

    Map<World, EnumMap<EventType, Map<String, Set<UUID>>>> deserialize(Map<String, Map<String, Map<String, Set<String>>>> data) {
        LoggerUtility.info(this, "Deserializing data...");
        final Map<World, EnumMap<EventType, Map<String, Set<UUID>>>> result = new HashMap<>();

        for (final Map.Entry<String, Map<String, Map<String, Set<String>>>> worldEntry : data.entrySet()) {
            final String worldName = worldEntry.getKey();
            final World world = Bukkit.getWorld(worldName);
            if (world == null) {
                LoggerUtility.warn(this, "World not found: %s".formatted(worldName));
                continue; // Skip if world is not loaded
            }

            final EnumMap<EventType, Map<String, Set<UUID>>> eventMap = new EnumMap<>(EventType.class);

            for (final Map.Entry<String, Map<String, Set<String>>> eventEntry : worldEntry.getValue().entrySet()) {
                final String eventTypeName = eventEntry.getKey();
                EventType eventType;
                try {
                    eventType = EventType.valueOf(eventTypeName);
                } catch (IllegalArgumentException e) {
                    LoggerUtility.warn(this, "Unknown EventType: %s".formatted(eventTypeName));
                    continue; // Skip unknown event types
                }

                final Map<String, Set<UUID>> ruleMap = new HashMap<>();
                for (final Map.Entry<String, Set<String>> ruleEntry : eventEntry.getValue().entrySet()) {
                    final Set<UUID> uuidSet = new HashSet<>();
                    for (final String uuidStr : ruleEntry.getValue()) {
                        try {
                            uuidSet.add(UUID.fromString(uuidStr));
                        } catch (IllegalArgumentException e) {
                            LoggerUtility.warn(this, "Invalid UUID: %s".formatted(uuidStr));
                        }
                    }
                    ruleMap.put(ruleEntry.getKey(), uuidSet);
                }

                eventMap.put(eventType, ruleMap);
            }

            result.put(world, eventMap);
        }

        return result;
    }
}
