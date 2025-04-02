package ru.vladimir.noctyss.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.api.EventAPI;
import ru.vladimir.noctyss.event.EventType;

import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public final class MessageConfig implements IConfig {
    private static final String FILE_NAME = "Messages.yml";

    // Settings
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();

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
    private String eventList;
    private String usage;

    // Messages
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

    public void init() {
        loadInternalSettings();
        parse();
    }

    @Override
    public void load() {
        save();
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
        parseInfoMessages();
        parseErrorMessages();
        parseEventMessages();
    }

    private void loadInternalSettings() {
        eventList = Arrays.stream(EventType.values())
                .map(Enum::name)
                .map(String::toLowerCase)
                .toList()
                .toString();
        usage = "/noctyss [start/stop/list/info/reload] <event> <world>";
    }

    private void parseInfoMessages() {
        activeEventListMsg = getFormattedMessage(INFO + "active-events", "Currently active events: {0}", getWorldNamesWithActiveEvents());
        commandUsage = getFormattedMessage(INFO + "command-usage", "Correct usage: {0}", usage);
        configReloaded = getMessage(INFO + "config-reloaded", "Configuration has been successfully reloaded.");
    }

    private Map<String, List<String>> getWorldNamesWithActiveEvents() {
        final Map<String, List<String>> worldNamesWithActiveEvents = new HashMap<>();

        for (final Map.Entry<World, List<EventType>> entry : EventAPI.getActiveEventsPerWorldEntries()) {
            final String worldName = entry.getKey().getName();
            final List<String> eventTypes = entry.getValue().stream().map(Enum::name).toList();
            worldNamesWithActiveEvents.put(worldName, eventTypes);
        }

        return worldNamesWithActiveEvents;
    }

    private void parseErrorMessages() {
        noPermission = getMessage(ERRORS + "no-permission", "You do not have permission to use this command.");
        unknownCommand = getMessage(ERRORS + "unknown-command", "This command does not exist. Please try again.");
        unknownEvent = getMessage(ERRORS + "unknown-event", "The specified event type does not exist.");
        unknownWorld = getMessage(ERRORS + "unknown-world", "The specified world could not be found.");
        playerOnly = getMessage(ERRORS + "player-only", "This command can only be used by players.");
    }
    private void parseEventMessages() {
        eventAlreadyActive = getFormattedMessage(EVENTS + "event-active", "The event {0} is already running in world {1}.");
        eventInactive = getFormattedMessage(EVENTS + "event-inactive", "The event {0} is not currently active in world {1}.");
        eventDisallowed = getFormattedMessage(EVENTS + "event-disallowed", "The event {0} is not allowed in world {1}.");
        eventStarted = getFormattedMessage(EVENTS + "event-started", "The event {0} has started in world {1}.");
        eventStopped = getFormattedMessage(EVENTS + "event-stopped", "The event {0} has been stopped in world {1}.");
        cannotSleep = getMessage(EVENTS + "sleep-prevention", "I don't want to sleep");
    }

    private Component getMessage(String path, String defaultMsg) {
        return parseColors(fileConfig.getString(path, defaultMsg));
    }

    private Component getFormattedMessage(String path, String defaultMsg, Object... values) {
        String message = fileConfig.getString(path, defaultMsg);
        message = MessageFormat.format(message, values);
        return parseColors(message);
    }

    private Component getFormattedMessage(String text, Object... values) {
        text = MessageFormat.format(text, values);
        return parseColors(text);
    }

    /**
     * Converts color codes (&6, &a, etc.) and hex colors (#FFA500) into MiniMessage format.
     */
    private Component parseColors(String message) {
        message = LEGACY_SERIALIZER.serialize(Component.text(message));
        return MINI_MESSAGE.deserialize(message);
    }

    public Component retrieveMessageNeedingFormat(Component message, Object... values) {
        return getFormattedMessage(PlainTextComponentSerializer.plainText().serialize(message), values);
    }

    @Override
    public void reload() {
        load();
    }
}
