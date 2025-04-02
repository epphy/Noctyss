package ru.vladimir.noctyss.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.api.EventAPI;
import ru.vladimir.noctyss.event.EventType;

import java.io.File;
import java.text.MessageFormat;
import java.util.*;

@Getter
@RequiredArgsConstructor
public final class MessageConfig implements IConfig {
    private static final String FILE_NAME = "MessageConfig.yml";

    // Sections
    private static final String GENERAL = "messages.general.";
    private static final String NIGHTMARE_NIGHT = "messages.nightmare-night.";

    // Dependency
    private final JavaPlugin plugin;

    // Configs
    private Map<String, Set<String>> activeEventList;
    private String eventList;
    private String usage;
    private FileConfiguration fileConfig;
    private File file;
    private Component eventListMsg;
    private Component activeEventListMsg;
    private Component noPermission;
    private Component unknownCommand;
    private Component unknownEvent;
    private Component unknownWorld;
    private Component commandUsage;
    private Component reloadedConfig;
    private Component playerOnly;
    private Component cannotSleep;

    @Override
    public void load() {
        save();
        parse();
    }

    private void save() {
        if (file == null) {
            file = new File(plugin.getDataFolder(), FILE_NAME);
        }

        if (!file.exists()) {
            plugin.saveResource(FILE_NAME, false);
        }

        fileConfig = YamlConfiguration.loadConfiguration(file);
    }

    private void parse() {
        loadInternalSettings();
        parseGeneral();
        parseNightmareNight();
    }

    private void loadInternalSettings() {
        activeEventList = getWorldNamesWithActiveEvents();
        eventList = Arrays.toString(EventType.values());
        usage = "/noctyss [start/stop/list/debug] <event> <world>";
    }

    private Map<String, Set<String>> getWorldNamesWithActiveEvents() {
        final Map<World, Set<EventType>> worldsWithActiveEvents = EventAPI.getWorldWithActiveEvents();
        final Map<String, Set<String>> worldNamesWithActiveEvents = new HashMap<>();
        for (final Map.Entry<World, Set<EventType>> entry : worldsWithActiveEvents.entrySet()) {
            final String worldName = entry.getKey().getName();
            final Set<String> eventTypeNames = new HashSet<>();
            for (final EventType eventType : entry.getValue()) {
                eventTypeNames.add(eventType.name());
            }
            worldNamesWithActiveEvents.put(worldName, eventTypeNames);
        }
        return worldNamesWithActiveEvents;
    }

    private void parseGeneral() {
        String eventListString = fileConfig.getString(GENERAL + "event-list", "Here is a list of existing events: {0}");
        eventListString = MessageFormat.format(eventListString, eventList);
        eventListMsg = stringToComponent(eventListString);

        String activeEventListString = fileConfig.getString(GENERAL + "active-event-list", "Currently active events: {0}");
        activeEventListString = MessageFormat.format(activeEventListString, activeEventList);
        activeEventListMsg = stringToComponent(activeEventListString);

        noPermission = stringToComponent(
                fileConfig.getString(GENERAL + "no-permission", "Oops! Seems like you don't have a permission to use this command"));

        unknownCommand = stringToComponent(
                fileConfig.getString(GENERAL + "unknown-command", "Uh, sorry, but this command, seems like, does not exist. Try again!"));

        unknownEvent = stringToComponent(
                fileConfig.getString(GENERAL + "unknown-event", "Unknown event type"));

        unknownWorld = stringToComponent(
                fileConfig.getString(GENERAL + "unknown-world", "Unknown world"));

        String commandUsageString = fileConfig.getString("command-usage", "Usage: {0}");
        commandUsageString = MessageFormat.format(commandUsageString, usage);
        commandUsage = stringToComponent(commandUsageString);

        reloadedConfig = stringToComponent(
                fileConfig.getString(GENERAL + "reloaded-config", "Config has been reloaded"));

        playerOnly = stringToComponent(
                fileConfig.getString(GENERAL + "player-only", "Hey! This command is for players only"));
    }

    private void parseNightmareNight() {
        cannotSleep = stringToComponent(
                fileConfig.getString(NIGHTMARE_NIGHT + "cannot-sleep", "I don't want to sleep"));
    }

    private Component stringToComponent(String s) {
        return Component.text(s);
    }

    @Override
    public void reload() {
        load();
    }
}
