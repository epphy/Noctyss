package ru.vladimir.noctyss.event.types.suddennight;

import com.comphenix.protocol.ProtocolManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.api.events.suddennight.SuddenNightEndEvent;
import ru.vladimir.noctyss.api.events.suddennight.SuddenNightStartEvent;
import ru.vladimir.noctyss.config.MessageConfig;
import ru.vladimir.noctyss.config.SuddenNightConfig;
import ru.vladimir.noctyss.event.EventManager;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.event.modules.Module;
import ru.vladimir.noctyss.event.modules.bukkitevents.BukkitEventService;
import ru.vladimir.noctyss.event.modules.environment.EnvironmentService;
import ru.vladimir.noctyss.event.modules.sounds.SoundService;
import ru.vladimir.noctyss.event.modules.spawnrate.SpawnRateService;
import ru.vladimir.noctyss.event.modules.time.TimeModifyService;
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
    private final SuddenNightConfig config;
    private final MessageConfig messageConfig;
    private final World world;

    @Override
    public void start() {
        registerModules();

        int started = 0;
        for (final Module module : modules) {
            module.start();
            started++;
            LoggerUtility.info(this, "Started module '%s' in '%s'"
                    .formatted(module.getClass().getSimpleName(), world.getName()));
        }

        pluginManager.callEvent(new SuddenNightStartEvent(world, true));
        LoggerUtility.info(this, "Started '%d' modules in '%s'"
                .formatted(started, world.getName()));
    }

    @Override
    public void stop() {
        int stopped = 0;
        for (final Module module : modules) {
            module.stop();
            stopped++;
            LoggerUtility.info(this, "Stopped module '%s' in '%s'"
                    .formatted(module.getClass().getSimpleName(), world.getName()));
        }

        pluginManager.callEvent(new SuddenNightEndEvent(world, true));
        LoggerUtility.info(this, "Stopped '%d' modules in '%s'"
                .formatted(stopped, world.getName()));
    }

    private void registerModules() {
        modules.add(
                new BukkitEventService.Builder(
                        plugin,
                        pluginManager,
                        world)
                        .addBedCancelEvent(messageConfig.getCannotSleep())
                        .build()
        );

        modules.add(
                new TimeModifyService.Builder(
                        plugin,
                        eventManager,
                        world,
                        EVENT_TYPE)
                        .addAbruptNight(config.getNightTimeModifyFrequency(), config.getNightLength(), new Random())
                        .build()
        );

        final SoundService.Builder soundServiceBuilder = new SoundService.Builder(
                plugin,
                pluginManager,
                protocolManager,
                world,
                EVENT_TYPE
        );

        soundServiceBuilder.addSoundMuter(
                config.getDisallowedSounds(), config.getAllowedSounds(), config.getRewindSound(), config.getAmbientStopFrequency());

        if (config.isMusicEnabled()) {
            soundServiceBuilder.addAmbiencePlayer(
                    config.getAmbientPlayDelayTicks(), config.getAmbientPlayFrequencyTicks(), config.getAllowedSounds(), new Random());
        }

        modules.add(soundServiceBuilder.build());

        modules.add(
                new SpawnRateService.Builder(
                        plugin,
                        pluginManager,
                        world)
                        .addNoSpawnRate()
                        .build()
        );

        modules.add(
                new EnvironmentService.Builder(
                        plugin,
                        pluginManager,
                        protocolManager,
                        world,
                        EVENT_TYPE)
                        .addLightDimmer()
                        .build()
        );
    }
}
