package ru.vladimir.noctyss.event.types.suddennight;

import com.comphenix.protocol.ProtocolManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.api.events.suddennight.SuddenNightEndEvent;
import ru.vladimir.noctyss.api.events.suddennight.SuddenNightStartEvent;
import ru.vladimir.noctyss.config.ConfigService;
import ru.vladimir.noctyss.event.EventManager;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.event.modules.Module;
import ru.vladimir.noctyss.event.modules.bukkitevents.BukkitEventService;
import ru.vladimir.noctyss.event.modules.environment.EnvironmentService;
import ru.vladimir.noctyss.event.modules.notification.NotificationService;
import ru.vladimir.noctyss.event.modules.sounds.SoundService;
import ru.vladimir.noctyss.event.modules.spawnrate.SpawnRateService;
import ru.vladimir.noctyss.event.modules.time.TimeService;
import ru.vladimir.noctyss.event.types.EventInstance;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@RequiredArgsConstructor
public final class SuddenNightInstance implements EventInstance {
    private static final EventType EVENT_TYPE = EventType.SUDDEN_NIGHT;
    private final Set<Module> modules = new HashSet<>();
    private final JavaPlugin plugin;
    private final PluginManager pluginManager;
    private final ProtocolManager protocolManager;
    private final EventManager eventManager;
    private final World world;

    @Override
    public void start() {
        registerModules();

        int started = 0;
        for (final Module module : modules) {
            module.start();
            started++;
            LoggerUtility.info(this, "Started '%s' in '%s'"
                    .formatted(module.getClass().getSimpleName(), world.getName()));
        }

        pluginManager.callEvent(new SuddenNightStartEvent(world, false));
        LoggerUtility.info(this, "Started all '%d' in '%s'"
                .formatted(started, world.getName()));
    }

    @Override
    public void stop() {
        pluginManager.callEvent(new SuddenNightEndEvent(world, false));

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
        addBukkitEventService();
        addTimeModifyService();
        addSoundService();
        addSpawnRateService();
        addEnvironmentService();
        addNotificationService();
    }

    private void addBukkitEventService() {
        modules.add(
                new BukkitEventService.Builder(
                        plugin,
                        pluginManager,
                        EVENT_TYPE,
                        world)
                        .addBedCancelEvent(
                                ConfigService.getMessageConfig().getCannotSleep())
                        .addChunkRefresher()
                        .build()
        );
    }

    private void addTimeModifyService() {
        modules.add(
                new TimeService.Builder(
                        plugin,
                        pluginManager,
                        eventManager,
                        world,
                        EVENT_TYPE)
                        .addAbruptNight(
                                ConfigService.getSuddenNightConfig().getNightTimeModifyFrequency(),
                                ConfigService.getSuddenNightConfig().getNightLength(),
                                new Random())
                        .build()
        );
    }

    private void addSoundService() {
        final var soundServiceBuilder = new SoundService.Builder(
                plugin, pluginManager, protocolManager, world, EVENT_TYPE);
        soundServiceBuilder.addSoundMuter(
                ConfigService.getSuddenNightConfig().getDisallowedSounds(),
                ConfigService.getSuddenNightConfig().getAllowedSounds(),
                ConfigService.getSuddenNightConfig().getRewindSound(),
                ConfigService.getSuddenNightConfig().getAmbientStopFrequency());

        if (ConfigService.getSuddenNightConfig().isMusicEnabled()) {
            soundServiceBuilder.addAmbiencePlayer(
                    ConfigService.getSuddenNightConfig().getAmbientPlayDelayTicks(),
                    ConfigService.getSuddenNightConfig().getAmbientPlayFrequencyTicks(),
                    ConfigService.getSuddenNightConfig().getAllowedSounds(),
                    new Random());
        }

        modules.add(soundServiceBuilder.build());
    }

    private void addSpawnRateService() {
        modules.add(new SpawnRateService.Builder(
                plugin,
                pluginManager,
                EVENT_TYPE,
                world)
                .addNoSpawnRate()
                .build()
        );
    }

    private void addEnvironmentService() {
        final var environmentServiceBuilder = new EnvironmentService.Builder(
                plugin, pluginManager, protocolManager, world, EVENT_TYPE);
        if (ConfigService.getSuddenNightConfig().isLightDimEnabled()) {
            environmentServiceBuilder.addLightingPocketModifier();
        }
        environmentServiceBuilder.addEntityAIKiller();
        modules.add(environmentServiceBuilder.build());
    }

    private void addNotificationService() {
        final var notificationServiceBuilder = new NotificationService.Builder(
                plugin, pluginManager, EVENT_TYPE, world);

        if (ConfigService.getSuddenNightConfig().isEndToastEnabled()) {
            notificationServiceBuilder.addToastEndEvent(
                    ConfigService.getSuddenNightConfig().isEndToastOneTime(),
                    ConfigService.getSuddenNightConfig().getEndToast());
        }

        modules.add(notificationServiceBuilder.build());
    }
}
