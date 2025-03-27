package ru.vladimir.noctyss.config;

import eu.endercentral.crazy_advancements.advancement.ToastNotification;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@RequiredArgsConstructor
class SuddenNightConfig implements AbstractConfig {
    private static final String FILE_NAME = "SuddenNight.yml";
    private final JavaPlugin plugin;
    private File file;
    private FileConfiguration fileConfig;

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
    }

    @Override
    public void reload() {

    }
}
