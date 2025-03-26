package ru.vladimir.votvproduction.event.types.nightmarenight;

import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.votvproduction.api.events.nightmarenight.NightmareNightEndEvent;
import ru.vladimir.votvproduction.api.events.nightmarenight.NightmareNightStartEvent;
import ru.vladimir.votvproduction.config.MessageConfig;
import ru.vladimir.votvproduction.config.NightmareNightConfig;
import ru.vladimir.votvproduction.event.EventManager;
import ru.vladimir.votvproduction.event.EventType;
import ru.vladimir.votvproduction.event.modules.EffectGiver;
import ru.vladimir.votvproduction.event.modules.Module;
import ru.vladimir.votvproduction.event.modules.SoundPlayer;
import ru.vladimir.votvproduction.event.modules.bukkitevents.BukkitEventService;
import ru.vladimir.votvproduction.event.modules.notification.NotificationService;
import ru.vladimir.votvproduction.event.modules.notification.storage.PlayerNotificationService;
import ru.vladimir.votvproduction.event.modules.spawnrate.SpawnRateService;
import ru.vladimir.votvproduction.event.modules.time.MidnightLoopModifier;
import ru.vladimir.votvproduction.event.types.EventInstance;
import ru.vladimir.votvproduction.utility.LoggerUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
public class NightmareNightInstance implements EventInstance {
    private static final List<Module> MODULES = new ArrayList<>();
    private final JavaPlugin plugin;
    private final PlayerNotificationService service;
    private final EventManager eventManager;
    private final PluginManager pluginManager;
    private final NightmareNightConfig config;
    private final MessageConfig messageConfig;
    private final World world;

    @Override
    public void start() {
        registerModules();
        pluginManager.callEvent(new NightmareNightStartEvent(world, true));
        for (final Module module : MODULES) {
            module.start();
        }
        LoggerUtility.info(this, "All modules started for world %s".formatted(world));
    }

    @Override
    public void stop() {
        pluginManager.callEvent(new NightmareNightEndEvent(world, true));
        for (final Module module : MODULES) {
            module.stop();
        }
        LoggerUtility.info(this, "All modules stopped for world %s".formatted(world));
    }

    private void registerModules() {
        if (config.isEffectEnabled()) {
            MODULES.add(new EffectGiver(
                    plugin,
                    world,
                    config.getEffects(),
                    config.getEffectGiveFrequency())
            );
        }

        if (config.isSoundEnabled()) {
            MODULES.add(new SoundPlayer(
                    plugin,
                    world,
                    new Random(),
                    config.getSounds(),
                    config.getSoundPlayFrequency()
            ));
        }

        if (config.isTimeEnabled()) {
            MODULES.add(new MidnightLoopModifier(
                    plugin,
                    world,
                    eventManager,
                    EventType.NIGHTMARE_NIGHT,
                    config.getTimeModifyFrequency(),
                    config.getNightLength()
            ));
        }

        MODULES.add(new BukkitEventService.Builder(
                plugin,
                pluginManager,
                world)
                .addBedCancelEvent(messageConfig.getCannotSleep())
                .build()
        );

        if (config.isSpawnRateEnabled()) {
            MODULES.add(new SpawnRateService.Builder(
                    plugin,
                    pluginManager,
                    world)
                    .addMonsterSpawnMultiplier(config.getMonsterMultiplier())
                    .build()
            );
        }

        if (config.isNotificationsEnabled()) {
            MODULES.add(new NotificationService.Builder(
                    plugin,
                    pluginManager,
                    service,
                    EventType.NIGHTMARE_NIGHT,
                    world)
                    .addToastEndEvent(config.getEndToast().oneTime(), config.getEndToast())
                    .build()
            );
        }
    }
}
