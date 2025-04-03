package ru.vladimir.noctyss.config;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.text.MessageFormat;

@Getter
@RequiredArgsConstructor
public final class MessageConfig implements IConfig {
    private static final String FILE_NAME = "Messages.yml";
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    // Section paths
    private static final String INFO = "messages.info.";
    private static final String ERRORS = "messages.errors.";
    private static final String EVENTS = "messages.events.";

    // Dependency
    private final @NonNull JavaPlugin plugin;

    // Config File
    private FileConfiguration fileConfig;
    private File file;

    // Internal Values
    private String usage;

    // Info
    private Component activeEventListMsg;
    private Component commandUsage;
    private Component configReloaded;

    // Error
    private Component noPermission;
    private Component unknownCommand;
    private Component unknownEvent;
    private Component unknownWorld;
    private Component playerOnly;

    // Event
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

        fileConfig = YamlConfiguration.loadConfiguration(file);
    }

    private void parse() {
        loadInternalSettings();
        parseInfoMessages();
        parseErrorMessages();
        parseEventMessages();
    }

    // INTERNAL
    private void loadInternalSettings() {
        usage = "/noctyss [start/stop/list/info/reload] <event> <world>";
    }

    // EXTERNAL
    private void parseInfoMessages() {
        activeEventListMsg = parseMessage(INFO + "active-events", "Currently active events: {0}");
        commandUsage = parseMessage(INFO + "command-usage", "Correct usage: {0}", usage);
        configReloaded = parseMessage(INFO + "config-reloaded", "&aConfiguration has been successfully reloaded.");
    }

    private void parseErrorMessages() {
        noPermission = parseMessage(ERRORS + "no-permission", "You do not have permission to use this command.");
        unknownCommand = parseMessage(ERRORS + "unknown-command", "This command does not exist. Please try again.");
        unknownEvent = parseMessage(ERRORS + "unknown-event", "The specified event type does not exist.");
        unknownWorld = parseMessage(ERRORS + "unknown-world", "The specified world could not be found.");
        playerOnly = parseMessage(ERRORS + "player-only", "This command can only be used by players.");
    }
    private void parseEventMessages() {
        eventAlreadyActive = parseMessage(EVENTS + "event-active", "The event is already running in world {0}.");
        eventInactive = parseMessage(EVENTS + "event-inactive", "The event is not currently active in world {0}.");
        eventDisallowed = parseMessage(EVENTS + "event-disallowed", "The event is not allowed in world {0}.");
        eventStarted = parseMessage(EVENTS + "event-started", "The event has started in world {0}.");
        eventStopped = parseMessage(EVENTS + "event-stopped", "The event has been stopped in world {0}.");
        cannotSleep = parseMessage(EVENTS + "sleep-prevention", "I don't want to sleep");
    }

    private Component parseMessage(String path, String defaultMsg, Object... values) {
        return processMessage(fileConfig.getString(path, defaultMsg), values);
    }

    public Component getMessage(Component message, Object... values) {
        return processMessage(MINI_MESSAGE.serialize(message), values);
    }

    private Component processMessage(String message, Object... values) {
        message = MessageFormat.format(message, values);
        return MINI_MESSAGE.deserialize(message);
    }

    @Override
    public void reload() {
        load();
    }
}
