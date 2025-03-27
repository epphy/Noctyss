package ru.vladimir.noctyss.event.types.nightmarenight;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.api.events.nightmarenight.NightmareNightEndEvent;
import ru.vladimir.noctyss.api.events.nightmarenight.NightmareNightStartEvent;
import ru.vladimir.noctyss.config.MessageConfig;
import ru.vladimir.noctyss.config.NightmareNightConfig;
import ru.vladimir.noctyss.event.EventManager;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.event.modules.EffectGiver;
import ru.vladimir.noctyss.event.modules.Module;
import ru.vladimir.noctyss.event.modules.SoundPlayer;
import ru.vladimir.noctyss.event.modules.bukkitevents.BukkitEventService;
import ru.vladimir.noctyss.event.modules.notification.NotificationService;
import ru.vladimir.noctyss.event.modules.notification.storage.PlayerNotificationService;
import ru.vladimir.noctyss.event.modules.spawnrate.SpawnRateService;
import ru.vladimir.noctyss.event.modules.time.TimeModifyService;
import ru.vladimir.noctyss.event.types.EventInstance;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ToString
@RequiredArgsConstructor
public class NightmareNightInstance implements EventInstance {
    private final List<Module> MODULES = new ArrayList<>();
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
        int registered = 0;
        pluginManager.callEvent(new NightmareNightStartEvent(world, true));
        for (final Module module : MODULES) {
            module.start();
            registered++;
            LoggerUtility.info(this, "Registered module '%s' in '%s'"
                    .formatted(module.getClass().getSimpleName(), world.getName()));
        }
        LoggerUtility.info(this, "All modules '%d' started in %s"
                .formatted(registered, world.getName()));
    }

    @Override
    public void stop() {
        pluginManager.callEvent(new NightmareNightEndEvent(world, true));
        int unregistered = 0;
        for (final Module module : MODULES) {
            module.stop();
            unregistered++;
            LoggerUtility.info(this, "Unregistered module '%s' in '%s'"
                    .formatted(module.getClass().getSimpleName(), world.getName()));
        }
        LoggerUtility.info(this, "All modules '%d' stopped in '%s'"
                .formatted(unregistered, world.getName()));
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
            MODULES.add(new TimeModifyService.Builder(
                    plugin,
                    eventManager,
                    world,
                    EventType.NIGHTMARE_NIGHT)
                    .addMidnightLoopModifier(config.getTimeModifyFrequency(), config.getNightLength())
                    .build()
            );
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
