package ru.vladimir.noctyss.config;

import lombok.experimental.UtilityClass;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.event.GlobalEventScheduler;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.EnumMap;
import java.util.Map;

@UtilityClass
public class ConfigService {
    private static final String CLASS_NAME = "ConfigService";
    private static final Map<Configs, IConfig> configs = new EnumMap<>(Configs.class);

    private enum Configs {
        GENERAL, SUDDEN_NIGHT, NIGHTMARE_NIGHT, MESSAGES
    }

    public static void init(JavaPlugin plugin) {
        register(plugin);
        load();
    }

    private static void register(JavaPlugin plugin) {
        final GeneralConfig generalConfig = new GeneralConfig(plugin, plugin.getConfig());
        final MessageConfig messageConfig = new MessageConfig(plugin);
        final NightmareNightConfig nightmareNightConfig = new NightmareNightConfig(plugin);
        final SuddenNightConfig suddenNightConfig = new SuddenNightConfig(plugin);

        configs.put(Configs.GENERAL, generalConfig);
        configs.put(Configs.MESSAGES, messageConfig);
        configs.put(Configs.NIGHTMARE_NIGHT, nightmareNightConfig);
        configs.put(Configs.SUDDEN_NIGHT, suddenNightConfig);

        LoggerUtility.info(CLASS_NAME, "All configs have been registered");
    }

    private static void load() {
        configs.values().forEach(IConfig::load);
        LoggerUtility.info(CLASS_NAME, "Configs have been loaded");
    }

    public static void reload(GlobalEventScheduler globalEventScheduler) {
        configs.values().forEach(IConfig::reload);
        globalEventScheduler.stop();
        globalEventScheduler.start();
        LoggerUtility.info(CLASS_NAME, "Configs have been reloaded");
    }

    public static GeneralConfig getGeneralConfig() {
        return (GeneralConfig) configs.get(Configs.GENERAL);
    }

    public static MessageConfig getMessageConfig() {
        return (MessageConfig) configs.get(Configs.MESSAGES);
    }

    public static NightmareNightConfig getNightmareNightConfig() {
        return (NightmareNightConfig) configs.get(Configs.NIGHTMARE_NIGHT);
    }

    public static SuddenNightConfig getSuddenNightConfig() {
        return (SuddenNightConfig) configs.get(Configs.SUDDEN_NIGHT);
    }
}