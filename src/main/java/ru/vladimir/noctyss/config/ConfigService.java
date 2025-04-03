package ru.vladimir.noctyss.config;

import lombok.experimental.UtilityClass;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.event.GlobalEventScheduler;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.EnumMap;
import java.util.Map;

@UtilityClass
public class ConfigService {
    private final String CLASS_NAME = "ConfigService";
    private final Map<Configs, IConfig> CONFIGS = new EnumMap<>(Configs.class);

    private enum Configs {
        GENERAL, SUDDEN_NIGHT, NIGHTMARE_NIGHT, MESSAGES
    }

    public void init(JavaPlugin plugin) {
        register(plugin);
    }

    public void loadGeneralConfig() {
        getGeneralConfig().load();
    }

    public void loadOtherConfigs() {
        load();
    }

    private void register(JavaPlugin plugin) {
        final GeneralConfig generalConfig = new GeneralConfig(plugin, plugin.getConfig());
        final MessageConfig messageConfig = new MessageConfig(plugin);
        final NightmareNightConfig nightmareNightConfig = new NightmareNightConfig(plugin);
        final SuddenNightConfig suddenNightConfig = new SuddenNightConfig(plugin);

        CONFIGS.put(Configs.GENERAL, generalConfig);
        CONFIGS.put(Configs.MESSAGES, messageConfig);
        CONFIGS.put(Configs.NIGHTMARE_NIGHT, nightmareNightConfig);
        CONFIGS.put(Configs.SUDDEN_NIGHT, suddenNightConfig);

        LoggerUtility.info(CLASS_NAME, "All configs have been registered");
    }

    private void load() {
        CONFIGS.values().forEach(IConfig::load);
        LoggerUtility.info(CLASS_NAME, "Configs have been loaded");
    }

    public void reload(GlobalEventScheduler globalEventScheduler) {
        CONFIGS.values().forEach(IConfig::reload);
        globalEventScheduler.stop();
        globalEventScheduler.start();
        LoggerUtility.info(CLASS_NAME, "Configs have been reloaded");
    }

    public GeneralConfig getGeneralConfig() {
        return (GeneralConfig) CONFIGS.get(Configs.GENERAL);
    }

    public MessageConfig getMessageConfig() {
        return (MessageConfig) CONFIGS.get(Configs.MESSAGES);
    }

    public NightmareNightConfig getNightmareNightConfig() {
        return (NightmareNightConfig) CONFIGS.get(Configs.NIGHTMARE_NIGHT);
    }

    public SuddenNightConfig getSuddenNightConfig() {
        return (SuddenNightConfig) CONFIGS.get(Configs.SUDDEN_NIGHT);
    }
}