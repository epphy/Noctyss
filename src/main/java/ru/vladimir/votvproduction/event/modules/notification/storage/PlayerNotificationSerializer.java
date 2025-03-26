package ru.vladimir.votvproduction.event.modules.notification.storage;

import org.bukkit.World;
import ru.vladimir.votvproduction.event.EventType;
import ru.vladimir.votvproduction.event.modules.notification.NotificationRule;

import java.util.*;

public class PlayerNotificationSerializer {

    Map<String, Map<String, Map<String, String>>> serialize(Map<World, Map<EventType, Map<NotificationRule, Set<UUID>>>> data) {
        final Map<String, Map<String, Map<String, String>>> serializedData = new HashMap<>();
        for (final Map.Entry<World, Map<EventType, Map<NotificationRule, Set<UUID>>>> entry : data.entrySet()) {

        }
    }

    Map<World, Map<EventType, Map<NotificationRule, Set<UUID>>>> deserialize() {

    }
}
