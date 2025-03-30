package ru.vladimir.noctyss.event.types.nightmarenight;

import com.comphenix.protocol.ProtocolManager;
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
import ru.vladimir.noctyss.event.modules.Module;
import ru.vladimir.noctyss.event.modules.bukkitevents.BukkitEventService;
import ru.vladimir.noctyss.event.modules.effects.EffectService;
import ru.vladimir.noctyss.event.modules.notification.NotificationService;
import ru.vladimir.noctyss.event.modules.notification.storage.PlayerNotificationService;
import ru.vladimir.noctyss.event.modules.sounds.SoundService;
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
    private static final EventType EVENT_TYPE = EventType.NIGHTMARE_NIGHT;
    private final List<Module> modules = new ArrayList<>();
    private final JavaPlugin plugin;
    private final ProtocolManager protocolManager;
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
        for (final Module module : modules) {
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
        for (final Module module : modules) {
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
            modules.add(new EffectService.Builder(
                    plugin,
                    pluginManager,
                    world,
                    EVENT_TYPE)
                    .addEffectGiveScheduler(config.getEffects(), config.getEffectGiveFrequency())
                    .build()
            );
        }

        if (config.isSoundEnabled()) {
            modules.add(new SoundService.Builder(
                    plugin,
                    pluginManager,
                    protocolManager,
                    world,
                    EVENT_TYPE)
                    .addSoundPlayScheduler(new Random(), config.getSounds(), config.getSoundPlayFrequency())
                    .build()
            );
        }

        if (config.isTimeEnabled()) {
            modules.add(new TimeModifyService.Builder(
                    plugin,
                    eventManager,
                    world,
                    EventType.NIGHTMARE_NIGHT)
                    .addMidnightLoopModifier(config.getTimeModifyFrequency(), config.getNightLength())
                    .build()
            );
        }

        modules.add(new BukkitEventService.Builder(
                plugin,
                pluginManager,
                world)
                .addBedCancelEvent(messageConfig.getCannotSleep())
                .build()
        );

        if (config.isSpawnRateEnabled()) {
            modules.add(new SpawnRateService.Builder(
                    plugin,
                    pluginManager,
                    world)
                    .addMonsterSpawnMultiplier(config.getMonsterMultiplier())
                    .build()
            );
        }

        if (config.isNotificationsEnabled()) {
            modules.add(new NotificationService.Builder(
                    plugin,
                    pluginManager,
                    service,
                    EventType.NIGHTMARE_NIGHT,
                    world)
                    .addToastEndEvent(config.isEndToastOneTime(), config.getEndToast())
                    .build()
            );
        }
    }
}
