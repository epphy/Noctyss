package ru.vladimir.votvproduction.event.types.nightmarenight;

import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.votvproduction.config.NightmareNightConfig;
import ru.vladimir.votvproduction.event.modules.EffectGiver;
import ru.vladimir.votvproduction.event.modules.Module;
import ru.vladimir.votvproduction.event.types.EventInstance;
import ru.vladimir.votvproduction.utility.LoggerUtility;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class NightmareNightInstance implements EventInstance {
    private static final List<Module> MODULES = new ArrayList<>();
    private final JavaPlugin plugin;
    private final NightmareNightConfig config;
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
    }
}
