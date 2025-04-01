package ru.vladimir.noctyss.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.text.MessageFormat;

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
    private final String eventList;
    private final String usage = "/noctyss [start/stop/list/debug] <event> <world>";
    private FileConfiguration fileConfig;
    private File file;
    private Component eventListMsg;
    private Component noPermission;
    private Component unknownCommand;
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
        parseGeneral();
        parseNightmareNight();
    }

    private void parseGeneral() {
        String eventListString = fileConfig.getString(GENERAL + "event-list", "Here is a list of existing events: {0}");
        eventListString = MessageFormat.format(eventListString, eventList);
        eventListMsg = stringToComponent(eventListString);
        noPermission = stringToComponent(
                fileConfig.getString(GENERAL + "no-permission", "Oops! Seems like you don't have a permission to use this command"));
        unknownCommand = stringToComponent(
                fileConfig.getString(GENERAL + "unknown-command", "Uh, sorry, but this command, seems like, does not exist. Try again!"));
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
