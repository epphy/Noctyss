package ru.vladimir.noctyss.event.types.nightmarenight;

import com.comphenix.protocol.ProtocolManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.vladimir.noctyss.api.events.nightmarenight.NightmareNightEndEvent;
import ru.vladimir.noctyss.api.events.nightmarenight.NightmareNightStartEvent;
import ru.vladimir.noctyss.config.MessageConfig;
import ru.vladimir.noctyss.config.NightmareNightConfig;
import ru.vladimir.noctyss.event.EventManager;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.event.modules.Module;
import ru.vladimir.noctyss.event.modules.bukkitevents.BukkitEventService;
import ru.vladimir.noctyss.event.modules.effects.EffectService;
import ru.vladimir.noctyss.event.modules.environment.EnvironmentService;
import ru.vladimir.noctyss.event.modules.notification.NotificationService;
import ru.vladimir.noctyss.event.modules.sounds.SoundService;
import ru.vladimir.noctyss.event.modules.spawnrate.SpawnRateService;
import ru.vladimir.noctyss.event.modules.time.TimeService;
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
    private final NightmareNightConfig config;
    private final MessageConfig messageConfig;
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
        registerEnvironmentModule();
        registerSoundModule();
        registerTimeModule();
        registerSpawnRateModule();
        registerNotificationModule();
        registerBukkitEventModule();
    }

    private void registerEnvironmentModule() {
        final var environmentService = new EnvironmentService.Builder(
                plugin,
                pluginManager,
                protocolManager,
                world,
                EVENT_TYPE
        );

        final var effectService = new EffectService.Builder(
                plugin,
                pluginManager,
                world,
                EVENT_TYPE
        );

        if (config.isDarkness()) {
            effectService.addEffectGiveScheduler(
                    List.of(new PotionEffect(
                    PotionEffectType.DARKNESS, (int) config.getDarknessGiveFrequency() - 20, 0, false, false)),
                    config.getDarknessGiveFrequency()
            );
        } else {
            environmentService.addLightingPacketModifier((byte) 0x01);
        }

        modules.add(effectService.build());
        modules.add(environmentService.build());
    }

    private void registerSoundModule() {
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

    private void registerTimeModule() {
        modules.add(
                new TimeService.Builder(
                        plugin,
                        pluginManager,
                        eventManager,
                        world,
                        EVENT_TYPE)
                        .addMidnightLoopModifier(config.getTimeModifyFrequency(), config.getNightLength())
                        .build()
        );
    }

    private void registerSpawnRateModule() {
        modules.add(new SpawnRateService.Builder(
                plugin,
                pluginManager,
                EVENT_TYPE,
                world)
                .addMonsterSpawnMultiplier(config.getMonsterMultiplier())
                .build()
        );
    }

    private void registerNotificationModule() {
        modules.add(new NotificationService.Builder(
                plugin,
                pluginManager,
                EVENT_TYPE,
                world)
                .addToastEndEvent(config.isEndToastOneTime(), config.getEndToast())
                .build()
        );
    }

    private void registerBukkitEventModule() {
        modules.add(new BukkitEventService.Builder(
                plugin,
                pluginManager,
                EVENT_TYPE,
                world)
                .addBedCancelEvent(messageConfig.getCannotSleep())
                .build()
        );
    }
}
