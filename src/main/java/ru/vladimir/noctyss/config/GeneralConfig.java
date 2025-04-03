package ru.vladimir.noctyss.config;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.*;

@Getter
@RequiredArgsConstructor
public final class GeneralConfig implements IConfig {
    private static final String SETTINGS = "settings.";
    private final JavaPlugin plugin;
    private final FileConfiguration fileConfig;
    private int debugLevel;
    private Map<World, List<EventType>> allowedEventWorlds;

    @Override
    public void load() {
        parse();
        validate();
    }

    private void parse() {
        debugLevel = fileConfig.getInt(SETTINGS + "debug-level", 0);
        allowedEventWorlds = getWorldMap(fileConfig.getConfigurationSection(SETTINGS + "allowed-worlds"));
    }

    @NonNull
    private Map<World, List<EventType>> getWorldMap(@Nullable ConfigurationSection section) {
        if (section == null) {
            LoggerUtility.warn(this, "List of worlds for allowed events is empty. No events will be handled.");
            return Collections.emptyMap();
        }

        Map<World, List<EventType>> worldMap = new HashMap<>();

        for (String worldName : section.getKeys(false)) {
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                LoggerUtility.warn(this, "World not found: %s".formatted(worldName));
                continue;
            }

            List<EventType> eventTypes = getEventTypes(section.getStringList(worldName));
            worldMap.put(world, eventTypes);
        }

        return worldMap;
    }

    @NonNull
    private List<EventType> getEventTypes(@NonNull List<String> eventTypeNames) {
        List<EventType> eventTypes = new ArrayList<>();

        for (String eventTypeName : eventTypeNames) {
            if (eventTypeName == null || eventTypeName.isBlank()) {
                LoggerUtility.warn(this, "Invalid event type name: (empty or null)");
                continue;
            }

            try {
                EventType eventType = EventType.valueOf(eventTypeName.toUpperCase().trim());
                eventTypes.add(eventType);
            } catch (IllegalArgumentException e) {
                LoggerUtility.warn(this, "Invalid event type name: %s. %s"
                        .formatted(eventTypeName, e.getMessage()));
            }
        }

        return eventTypes;
    }

    private void validate() {
        if (debugLevel < 0 || debugLevel > 2) {
            LoggerUtility.warn(this, "Invalid debug level '%d'. Setting to default".formatted(debugLevel));
            debugLevel = 0;
        }

        if (allowedEventWorlds.isEmpty()) {
            plugin.reloadConfig();
            LoggerUtility.warn(this, "List of allowed event worlds is empty. No events will be handled.");
        }
    }

    @Override
    public void reload() {
        load();
    }
}
