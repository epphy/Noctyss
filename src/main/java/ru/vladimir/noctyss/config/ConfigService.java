package ru.vladimir.noctyss.config;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.HashMap;
import java.util.Map;

@Getter
@UtilityClass
public class ConfigService {
    private static final String CLASS_NAME = "ConfigService";
    private static final ConfigService INSTANCE = new ConfigService();
    private static final Map<String, AbstractConfig> CONFIGS = new HashMap<>();
    private GeneralConfig generalConfig;
    private MessageConfig messageConfig;
    private NightmareNightConfig nightmareNightConfig;
    private SuddenNightConfig suddenNightConfig;

    public static void init(JavaPlugin plugin) {
        register(plugin);
        load();
    }

    private static void register(JavaPlugin plugin) {
        generalConfig = new GeneralConfig(plugin.getConfig());
        messageConfig = new MessageConfig(plugin);
        nightmareNightConfig = new NightmareNightConfig(plugin);
        suddenNightConfig = new SuddenNightConfig(plugin);

        CONFIGS.put("General", generalConfig);
        CONFIGS.put("Message", messageConfig);
        CONFIGS.put("NightmareNight", nightmareNightConfig);
        CONFIGS.put("SuddenNight", suddenNightConfig);

        LoggerUtility.info(CLASS_NAME, "All configs have been registered");
    }

    private static void load() {
        for (final Map.Entry<String, AbstractConfig> entry : CONFIGS.entrySet()) {
            entry.getValue().load();
        }
        LoggerUtility.info(CLASS_NAME, "Configs have been loaded");
    }

    public static void reload() {
        for (final Map.Entry<String, AbstractConfig> entry : CONFIGS.entrySet()) {
            entry.getValue().reload();
        }
        LoggerUtility.info(CLASS_NAME, "Configs have been reloaded");
    }

    public static ConfigService getInstance() {
        return INSTANCE;
    }
}