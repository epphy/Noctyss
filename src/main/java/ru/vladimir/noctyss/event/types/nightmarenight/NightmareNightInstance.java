package ru.vladimir.noctyss.event.types.nightmarenight;

import com.comphenix.protocol.ProtocolManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.api.events.nightmarenight.NightmareNightEndEvent;
import ru.vladimir.noctyss.api.events.nightmarenight.NightmareNightStartEvent;
import ru.vladimir.noctyss.config.ConfigService;
import ru.vladimir.noctyss.event.EventManager;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.event.modules.Module;
import ru.vladimir.noctyss.event.modules.bukkitevents.BukkitEventService;
import ru.vladimir.noctyss.event.modules.effects.EffectService;
import ru.vladimir.noctyss.event.modules.notification.NotificationService;
import ru.vladimir.noctyss.event.modules.sounds.SoundService;
import ru.vladimir.noctyss.event.modules.spawnrate.SpawnRateService;
import ru.vladimir.noctyss.event.modules.time.TimeModifyService;
import ru.vladimir.noctyss.event.types.EventInstance;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
public final class NightmareNightInstance implements EventInstance {
    private static final EventType EVENT_TYPE = EventType.NIGHTMARE_NIGHT;
    private final List<Module> modules = new ArrayList<>();
    private final JavaPlugin plugin;
    private final ProtocolManager protocolManager;
    private final EventManager eventManager;
    private final PluginManager pluginManager;
    private final World world;

    @Override
    public void start() {
        registerModules();

        int registered = 0;
        for (final Module module : modules) {
            module.start();
            registered++;
            LoggerUtility.info(this, "Started '%s' in '%s'"
                    .formatted(module.getClass().getSimpleName(), world.getName()));
        }

        pluginManager.callEvent(new NightmareNightStartEvent(world, false));
        LoggerUtility.info(this, "Started all '%d' in %s"
                .formatted(registered, world.getName()));
    }

    @Override
    public void stop() {
        pluginManager.callEvent(new NightmareNightEndEvent(world, false));

        int stopped = 0;
        for (final Module module : modules) {
            module.stop();
            stopped++;
            LoggerUtility.info(this, "Stopped '%s' in '%s'"
                    .formatted(module.getClass().getSimpleName(), world.getName()));
        }
        LoggerUtility.info(this, "Stopped all '%d' in '%s'"
                .formatted(stopped, world.getName()));
    }

    private void registerModules() {
        if (ConfigService.getNightmareNightConfig().isEffectEnabled()) {
            modules.add(new EffectService.Builder(
                    plugin,
                    pluginManager,
                    world,
                    EVENT_TYPE)
                    .addEffectGiveScheduler(
                            ConfigService.getNightmareNightConfig().getEffects(),
                            ConfigService.getNightmareNightConfig().getEffectGiveFrequency())
                    .build()
            );
        }

        if (ConfigService.getNightmareNightConfig().isSoundEnabled()) {
            modules.add(new SoundService.Builder(
                    plugin,
                    pluginManager,
                    protocolManager,
                    world,
                    EVENT_TYPE)
                    .addSoundPlayScheduler(new Random(),
                            ConfigService.getNightmareNightConfig().getSounds(),
                            ConfigService.getNightmareNightConfig().getSoundPlayFrequency())
                    .build()
            );
        }

        if (ConfigService.getNightmareNightConfig().isTimeEnabled()) {
            modules.add(new TimeModifyService.Builder(
                    plugin,
                    pluginManager,
                    eventManager,
                    world,
                    EventType.NIGHTMARE_NIGHT)
                    .addMidnightLoopModifier(
                            ConfigService.getNightmareNightConfig().getTimeModifyFrequency(),
                            ConfigService.getNightmareNightConfig().getNightLength())
                    .build()
            );
        }

        modules.add(new BukkitEventService.Builder(
                plugin,
                pluginManager,
                EVENT_TYPE,
                world)
                .addBedCancelEvent(ConfigService.getMessageConfig().getCannotSleep())
                .build()
        );

        if (ConfigService.getNightmareNightConfig().isSpawnRateEnabled()) {
            modules.add(new SpawnRateService.Builder(
                    plugin,
                    pluginManager,
                    EVENT_TYPE,
                    world)
                    .addMonsterSpawnMultiplier(
                            ConfigService.getNightmareNightConfig().getMonsterMultiplier())
                    .build()
            );
        }

        if (ConfigService.getNightmareNightConfig().isNotificationsEnabled()) {
            modules.add(new NotificationService.Builder(
                    plugin,
                    pluginManager,
                    EventType.NIGHTMARE_NIGHT,
                    world)
                    .addToastEndEvent(
                            ConfigService.getNightmareNightConfig().isEndToastOneTime(),
                            ConfigService.getNightmareNightConfig().getEndToast())
                    .build()
            );
        }
    }
}
