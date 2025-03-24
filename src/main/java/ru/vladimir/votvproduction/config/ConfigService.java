package ru.vladimir.votvproduction.config;

import ru.vladimir.votvproduction.utility.LoggerUtility;

import java.util.ArrayList;
import java.util.List;

public record ConfigService(GeneralConfig generalConfig,
                            NightmareNightConfig nightmareNightConfig,
                            MessageConfig messageConfig) {
    private static final List<AbstractConfig> configs = new ArrayList<>();

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
        configs.add(generalConfig);
        configs.add(messageConfig);
        configs.add(nightmareNightConfig);
        LoggerUtility.info(this, "All configs have been registered: %s".formatted(configs));
    }
}