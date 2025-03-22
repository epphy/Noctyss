package ru.vladimir.votvproduction.event.types.nightmarenight;

import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.votvproduction.config.MessageConfig;
import ru.vladimir.votvproduction.config.NightmareNightConfig;
import ru.vladimir.votvproduction.event.modules.EffectGiver;
import ru.vladimir.votvproduction.event.modules.Module;
import ru.vladimir.votvproduction.event.modules.SoundPlayer;
import ru.vladimir.votvproduction.event.modules.bukkitevents.BedCancelEvent;
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
    private final NightmareNightConfig config;
    private final MessageConfig messageConfig;
    private final World world;

    @Override
    public void start() {
        registerModules();
        for (final Module module : MODULES) {
            module.start();
        }
        LoggerUtility.info(this, "All modules started for world %s".formatted(world));
    }

    @Override
    public void stop() {
        for (final Module module : MODULES) {
            module.stop();
        }
        LoggerUtility.info(this, "All modules stopped for world %s".formatted(world));
    }

    private void registerModules() {
        MODULES.add(new EffectGiver(
                plugin,
                world,
                config.getEffects(),
                config.getEffectGiveFrequency())
        );
        MODULES.add(new SoundPlayer(
                plugin,
                world,
                new Random(),
                config.getSounds(),
                config.getSoundPlayFrequency()
        ));
        MODULES.add(new MidnightLoopModifier(
                plugin,
                world,
                config.getTimeModifyFrequency(),
                config.getNightLength()
        ));

    }
}
