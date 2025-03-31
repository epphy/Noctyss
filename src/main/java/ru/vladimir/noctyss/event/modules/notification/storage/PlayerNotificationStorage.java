package ru.vladimir.noctyss.event.modules.notification.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
class PlayerNotificationStorage {
    private static final String FILE_NAME = "PlayersNotifications.json";
    private static final TypeToken<Map<String, Map<String, Map<String, Set<String>>>>> TYPE_TOKEN = new TypeToken<>(){};
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

    void store(Map<String, Map<String, Map<String, Set<String>>>> playerNotifications) {
        final GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        final Gson gson = builder.create();

        try (final FileWriter writer = new FileWriter(file)) {
            writer.write(gson.toJson(playerNotifications));
        } catch (IOException e) {
            LoggerUtility.err(this, "Could not store player ids in the storage: %s".formatted(e.getMessage()));
            e.printStackTrace();
        }
    }

    Map<String, Map<String, Map<String, Set<String>>>> retrieve() {
        final GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        final Gson gson = builder.create();

        try (final FileReader reader = new FileReader(file)) {
            final Map<String, Map<String, Map<String, Set<String>>>> data = gson.fromJson(reader, TYPE_TOKEN);
            if (data == null) return new HashMap<>();
            else return data;
        } catch (IOException e) {
            LoggerUtility.err(this, "Could not store player ids in the storage: %s".formatted(e.getMessage()));
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}
