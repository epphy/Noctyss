package ru.vladimir.votvproduction.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import ru.vladimir.votvproduction.event.EventType;
import ru.vladimir.votvproduction.utility.LoggerUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public final class GeneralConfig implements AbstractConfig {
    private static final String SETTINGS = "settings.";
    private final FileConfiguration fileConfig;
    private boolean enabled;
    private int debugLevel;
    private Map<World, List<EventType>> allowedEventWorlds;

    @Override
    public void load() {
        parse();
        validate();
    }

    private void parse() {
        enabled = fileConfig.getBoolean(SETTINGS + "plugin-enabled", true);
        debugLevel = fileConfig.getInt(SETTINGS + "debug-level", 0);
        allowedEventWorlds = getWorldMap(fileConfig.getMapList(SETTINGS + "allowed-worlds"));
    }

    private Map<World, List<EventType>> getWorldMap(List<Map<?, ?>> worldStringMaps) {
        if (worldStringMaps.isEmpty()) {
            LoggerUtility.warn(this,
                    "List of worlds for allowed events is empty and therefore events won't be used");
            return Map.of();
        }

        final Map<World, List<EventType>> worldMap = new HashMap<>();

        for (final Map<?, ?> worldStringMap : worldStringMaps) {
            for (final Map.Entry<?, ?> entry : worldStringMap.entrySet()) {

                if (!(entry.getKey() instanceof final String worldName)) {
                    LoggerUtility.warn(this, "Invalid world name type: %s"
                            .formatted(entry.getKey()));
                    continue;
                }

                if (!(entry.getValue() instanceof final List<?> rawEventTypeNames)) {
                    LoggerUtility.warn(this, "Invalid event type list for world %s: %s"
                            .formatted(worldMap, entry.getValue()));
                    continue;
                }

                final List<String> eventTypeNames = new ArrayList<>();
                for (final Object obj : rawEventTypeNames) {
                    if (obj instanceof String eventTypeName) {
                        eventTypeNames.add(eventTypeName);
                    } else {
                        LoggerUtility.warn(this, "Invalid event type found in list for world %s: %s"
                                .formatted(worldName, obj));
                    }
                }

                final World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    LoggerUtility.warn(this, "World not found: %s"
                            .formatted(worldName));
                    continue;
                }

                final List<EventType> eventTypes = getEventTypes(eventTypeNames);

                worldMap.put(world, eventTypes);
            }
        }

        return worldMap;
    }

    private List<EventType> getEventTypes(List<String> eventTypeNames) {
        final List<EventType> eventTypes = new ArrayList<>();
        for (final String eventTypeName : eventTypeNames) {
            try {
                final EventType eventType = EventType.valueOf(eventTypeName.toUpperCase().trim());
                eventTypes.add(eventType);
            } catch (IllegalArgumentException | NullPointerException e) {
                LoggerUtility.warn(this, "Invalid event type name: %s. %s"
                        .formatted(eventTypeName, e));
            }
        }
        return eventTypes;
    }

    private void validate() {
        if (debugLevel < 0 || debugLevel > 2) {
            LoggerUtility.warn(this, "Invalid debug level '%d'. Setting to default"
                    .formatted(debugLevel));
            debugLevel = 0;
        }

        if (allowedEventWorlds.isEmpty()) {
            LoggerUtility.warn(this,
                    "List of allowed event worlds is null and therefore no events will be handled");
        }
    }

    @Override
    public void reload() {
        load();
    }
}
