package ru.vladimir.votvproduction.event.modules.notification.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
class PlayerNotificationStorage {
    private static final String FILE_NAME = "PlayersNotifications.json";
    private final JavaPlugin plugin;
    private File file;

    void save() {
        if (file == null) {
            file = new File(plugin.getDataFolder(), FILE_NAME);
        }

        if (!file.exists()) {
            plugin.saveResource(FILE_NAME, false);
        }
    }

    void storeWorld(Map<String, Map<String, Map<String, String>>> playerNotifications) {
        final GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
    }

    void storeEventType(String worldName, Map<String, Map<String, String>> playerNotifications) {

    }

    void storeNotificationRule(String worldName, String eventTypeName, Map<String, String> playerNotifications) {

    }

    void storePlayerIds(String worldName, String eventTypeName, String notificationRuleName, List<String> playerIds) {

    }

    Map<String, Map<String, Map<String, String>>> retrieve() {

    }
}
