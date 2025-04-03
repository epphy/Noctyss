package ru.vladimir.noctyss.config;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.EnumMap;
import java.util.Map;

// ToDo: Deal with the conflict between EventAPI, MessageConfig, and GeneralConfig loading.
/**
 * A utility class that manages the registration, loading, and reloading of
 * various configuration types within the application. The configurations are
 * stored in an internal map and accessed or modified as needed.
 * <p>
 * This class provides methods to initialize, load, and reload configurations
 * such as general settings, message configurations, and specific event-related
 * configurations. The `ConfigService` class ensures configurations are properly
 * loaded and validated before usage.
 * <p>
 * The configurations are represented by implementations of the `IConfig` interface.
 * <p>
 * Lastly, it's important to notice that currently to load the plugin properly,
 * general config should be loaded separately and only then we load the rest.
 * The reason is we need {@code EventAPI} for {@code MessageConfig} while
 * {@code EventAPI} needs {@code GeneralConfig} to be loaded. Hence, we have
 * a little problem here which should be addressed later on.
 */
@UtilityClass
public class ConfigService {
    private final String CLASS_NAME = "ConfigService";
    private final Map<ConfigType, IConfig> CONFIGS = new EnumMap<>(ConfigType.class);

    private enum ConfigType {
        GENERAL, SUDDEN_NIGHT, NIGHTMARE_NIGHT, MESSAGES
    }

    public void init(@NonNull JavaPlugin plugin) {
        register(plugin);
    }

    public void loadGeneralConfig() {
        getGeneralConfig().load();
    }

    public void loadOtherConfigs(@NonNull JavaPlugin plugin) {
        load(plugin);
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