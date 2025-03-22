package ru.vladimir.votvproduction.config;

public record ConfigService(GeneralConfig generalConfig, NightmareNightConfig nightmareNightConfig,
                            SuddenNightConfig suddenNightConfig, MessageConfig messageConfig) {
}
