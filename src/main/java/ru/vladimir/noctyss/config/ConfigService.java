package ru.vladimir.noctyss.config;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.HashMap;
import java.util.Map;

// TODO
@UtilityClass
public class ConfigService {
    private static final String CLASS_NAME = "ConfigService";
    private final Map<String, IConfig> configs = new HashMap<>();
    @Getter
    private GeneralConfig generalConfig;
    @Getter
    private MessageConfig messageConfig;
    @Getter
    private NightmareNightConfig nightmareNightConfig;
    @Getter
    private SuddenNightConfig suddenNightConfig;

    public static void init(JavaPlugin plugin) {
        register(plugin);
        load();
    }

    private static void register(JavaPlugin plugin) {
        plugin.saveDefaultConfig();
        generalConfig = new GeneralConfig(plugin.getConfig());
        messageConfig = new MessageConfig(plugin);
        nightmareNightConfig = new NightmareNightConfig(plugin);
        suddenNightConfig = new SuddenNightConfig(plugin);

        configs.put("General", generalConfig);
        configs.put("Message", messageConfig);
        configs.put("NightmareNight", nightmareNightConfig);
        configs.put("SuddenNight", suddenNightConfig);

        LoggerUtility.info(CLASS_NAME, "All configs have been registered");
    }

    private static void load() {
        configs.values().forEach(IConfig::load);
        LoggerUtility.info(CLASS_NAME, "Configs have been loaded");
    }

    public static void reload() {
        configs.values().forEach(IConfig::reload);
        LoggerUtility.info(CLASS_NAME, "Configs have been reloaded");
    }

}