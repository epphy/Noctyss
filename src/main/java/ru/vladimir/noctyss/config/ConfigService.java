package ru.vladimir.noctyss.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public final class ConfigService {
    private final JavaPlugin plugin;
    private final List<AbstractConfig> configs = new ArrayList<>();
    private GeneralConfig generalConfig;
    private MessageConfig messageConfig;
    private NightmareNightConfig nightmareNightConfig;
    private SuddenNightConfig suddenNightConfig;

    public void init() {
        register();
        load();
        LoggerUtility.info(this, "ConfigService has been initialised");
    }

    public void load() {
        for (AbstractConfig config : configs) {
            config.load();
        }
        LoggerUtility.info(this, "All configs have been loaded");
    }

    public void reload() {
        for (AbstractConfig config : configs) {
            config.reload();
        }
        LoggerUtility.info(this, "All configs have been reloaded");
    }

    private void register() {
        plugin.saveDefaultConfig();

        generalConfig = new GeneralConfig(plugin.getConfig());
        messageConfig = new MessageConfig(plugin);
        nightmareNightConfig = new NightmareNightConfig(plugin);
        suddenNightConfig = new SuddenNightConfig(plugin);

        configs.add(generalConfig);
        configs.add(messageConfig);
        configs.add(nightmareNightConfig);
        configs.add(suddenNightConfig);

        LoggerUtility.debug(this, "All configs have been registered: %s".formatted(configs));
    }
}