package ru.vladimir.votvproduction.event.modules.notification.storage;

import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Map;

@RequiredArgsConstructor
public class PlayerNotificationStorage {
    private static final String FILE_NAME = "PlayersNotifications.json";
    private final JavaPlugin plugin;
    private File file;

    public void save() {
        if (file == null) {
            file = new File(plugin.getDataFolder(), FILE_NAME);
        }

        if (!file.exists()) {
            plugin.saveResource(FILE_NAME, false);
        }
    }

    public void store(Map<String, Map<String, Map<String, String>>> playerNotifications) {

    }

    public Map<String, Map<String, Map<String, String>>> retrieve() {

    }
}
