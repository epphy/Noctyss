package ru.vladimir.votvproduction.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter
@RequiredArgsConstructor
public final class MessageConfig implements AbstractConfig {
    private static final String FILE_NAME = "MessageConfig.yml";
    private static final String NIGHTMARE_NIGHT = "messages.nightmare-night.";
    private final JavaPlugin plugin;
    private FileConfiguration fileConfig;
    private File file;
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
