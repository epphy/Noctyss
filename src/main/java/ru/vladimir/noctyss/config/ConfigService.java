package ru.vladimir.noctyss.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.EnumMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigService {

    private static final String CLASS_NAME = ConfigService.class.getSimpleName();
    private static final Map<ConfigType, IConfig> CONFIGS = new EnumMap<>(ConfigType.class);
    private static ConfigService instance;

    private enum ConfigType {
        GENERAL, SUDDEN_NIGHT, NIGHTMARE_NIGHT, MESSAGES
    }

    public static ConfigService getInstance() {
        if (instance == null) {
            throw new IllegalStateException("%s is not initialised yet. Call init method first");
        }
        return instance;
    }

    public static void init(@NonNull JavaPlugin plugin) {
        instance = new ConfigService();
        instance.register(plugin);
        instance.load(plugin);
    }

    public static void unload() {
        CONFIGS.clear();
        instance = null;
    }

    private void register(@NonNull JavaPlugin plugin) {
        CONFIGS.put(ConfigType.GENERAL, new GeneralConfig(plugin, plugin.getConfig()));
        CONFIGS.put(ConfigType.MESSAGES, new MessageConfig(plugin));
        CONFIGS.put(ConfigType.NIGHTMARE_NIGHT, new NightmareNightConfig(plugin));
        CONFIGS.put(ConfigType.SUDDEN_NIGHT, new SuddenNightConfig(plugin));
    }

    private void load(@NonNull JavaPlugin plugin) {
        plugin.saveDefaultConfig();
        CONFIGS.values().forEach(IConfig::load);
        LoggerUtility.info(CLASS_NAME, "Configs have been loaded");
    }

    public void reload() {
        CONFIGS.values().forEach(IConfig::reload);
        LoggerUtility.info(CLASS_NAME, "Configs have been reloaded");
    }

    @NonNull
    public GeneralConfig getGeneralConfig() {
        return getConfig(ConfigType.GENERAL, GeneralConfig.class);
    }

    @NonNull
    public MessageConfig getMessageConfig() {
        return getConfig(ConfigType.MESSAGES, MessageConfig.class);
    }

    @NonNull
    public NightmareNightConfig getNightmareNightConfig() {
        return getConfig(ConfigType.NIGHTMARE_NIGHT, NightmareNightConfig.class);
    }

    @NonNull
    public SuddenNightConfig getSuddenNightConfig() {
        return getConfig(ConfigType.SUDDEN_NIGHT, SuddenNightConfig.class);
    }

    @NonNull
    @SuppressWarnings("unchecked")
    private <T extends IConfig> T getConfig(@NonNull ConfigType configType, @NonNull Class<T> clazz) {
        IConfig config = CONFIGS.get(configType);

        if (!(clazz.isInstance(config))) {
            throw new IllegalStateException("Config '%s' is not of expected type: %s"
                    .formatted(config, clazz.getSimpleName()));
        }

        return (T) config;
    }
}