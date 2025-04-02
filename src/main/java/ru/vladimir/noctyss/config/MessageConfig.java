package ru.vladimir.noctyss.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public final class MessageConfig implements IConfig {
    private static final String FILE_NAME = "Messages.yml";

    // Section paths
    private static final String INFO = "messages.info.";
    private static final String ERRORS = "messages.errors.";
    private static final String EVENTS = "messages.events.";

    // Dependency
    private final JavaPlugin plugin;

    // Config File
    private FileConfiguration fileConfig;
    private File file;

    // Internal Values
    private Map<String, List<String>> activeEventList;
    private String eventList;
    private String usage;

    // Messages
    private Component eventListMsg;
    private Component activeEventListMsg;
    private Component commandUsage;
    private Component configReloaded;

    private Component noPermission;
    private Component unknownCommand;
    private Component unknownEvent;
    private Component unknownWorld;
    private Component playerOnly;

    private Component eventAlreadyActive;
    private Component eventInactive;
    private Component eventDisallowed;
    private Component eventStarted;
    private Component eventStopped;

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
    }

    private void parse() {
        loadInternalSettings();
        parseInfoMessages();
        parseErrorMessages();
        parseEventMessages();
    }

    private void loadInternalSettings() {}
    private void parseInfoMessages() {}
    private void parseErrorMessages() {}
    private void parseEventMessages() {}

    @Override
    public void reload() {
        load();
    }
}
