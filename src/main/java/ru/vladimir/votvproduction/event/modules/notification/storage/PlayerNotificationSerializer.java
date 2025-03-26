package ru.vladimir.votvproduction.event.modules.notification.storage;

import org.bukkit.Bukkit;
import org.bukkit.World;
import ru.vladimir.votvproduction.event.EventType;

import java.util.*;
import java.util.stream.Collectors;

class PlayerNotificationSerializer {

    Map<String, Map<String, Map<String, Set<String>>>> serialize(Map<World, Map<EventType, Map<String, Set<UUID>>>> data) {
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
                                                        ruleEntry -> ruleEntry.getKey().getClass().getSimpleName(), // Rule name
                                                        ruleEntry -> ruleEntry.getValue().stream()
                                                                .filter(Objects::nonNull)
                                                                .map(UUID::toString) // Convert UUIDs to strings
                                                                .collect(Collectors.toSet())
                                                ))
                                ))
                ));
    }

    Map<World, Map<EventType, Map<String, Set<UUID>>>> deserialize(Map<String, Map<String, Map<String, Set<String>>>> data) {
        return data.entrySet().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        entry -> Bukkit.getWorld(entry.getKey()), // World name as key
                        entry -> entry.getValue().entrySet().stream()
                                .filter(Objects::nonNull)
                                .collect(Collectors.toMap(
                                        eventEntry -> EventType.valueOf(eventEntry.getKey()), // EventType name
                                        eventEntry -> eventEntry.getValue().entrySet().stream()
                                                .filter(Objects::nonNull)
                                                .collect(Collectors.toMap(
                                                        Map.Entry::getKey, // Rule name
                                                        ruleEntry -> ruleEntry.getValue().stream()
                                                                .filter(Objects::nonNull)
                                                                .map(UUID::fromString) // Convert strings to UUIDs
                                                                .collect(Collectors.toSet())
                                                ))
                                ))
                ));
    }
}
