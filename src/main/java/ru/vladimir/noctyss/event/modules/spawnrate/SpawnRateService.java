package ru.vladimir.noctyss.event.modules.spawnrate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.event.Controllable;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.event.modules.Module;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.ArrayList;
import java.util.List;

public class SpawnRateService implements Module {
    private final JavaPlugin plugin;
    private final PluginManager pluginManager;
    private final EventType eventType;
    private final World world;
    private final List<SpawnRule> spawnRules;

    private SpawnRateService(Builder builder) {
        this.plugin = builder.getPlugin();
        this.pluginManager = builder.getPluginManager();
        this.eventType = builder.getEventType();
        this.world = builder.getWorld();
        this.spawnRules = builder.getSpawnRules();
    }

    @Override
    public void start() {
        int registered = 0;
        for (final SpawnRule spawnRule : spawnRules) {

            if (spawnRule instanceof Controllable) {
                ((Controllable) spawnRule).start();
            }

            if (spawnRule instanceof Listener) {
                pluginManager.registerEvents((Listener) spawnRule, plugin);
            }

            registered++;
            LoggerUtility.info(this, "Added '%s' in '%s' for '%s'"
                    .formatted(spawnRule.getClass().getSimpleName(), world.getName(), eventType.name()));
        }
        LoggerUtility.info(this, "Added all '%d' in '%s' for '%s'"
                .formatted(registered, world.getName(), eventType.name()));
    }

    @Override
    public void stop() {
        try {
            int unregistered = 0;
            for (final SpawnRule rule : spawnRules) {

                if (rule instanceof Controllable) {
                    ((Controllable) rule).stop();
                }

                if (rule instanceof Listener) {
                    HandlerList.unregisterAll((Listener) rule);
                }

                unregistered++;
                LoggerUtility.info(this, "Unregistered '%s' spawn rule in '%s'"
                        .formatted(rule.getClass().getSimpleName(), world.getName()));
            }
            LoggerUtility.info(this, "Unregistered '%d' spawn rules in '%s'".formatted(unregistered, world.getName()));
        } catch (Exception e) {
            throw new IllegalStateException("An error occurred while loading spawn rules", e);
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class Builder {
        private final JavaPlugin plugin;
        private final PluginManager pluginManager;
        private final EventType eventType;
        private final World world;
        private final List<SpawnRule> spawnRules = new ArrayList<>();

        public Builder addMonsterSpawnMultiplier(int multiplier) {
            MonsterSpawnMultiplier spawnRule = new MonsterSpawnMultiplier(world, multiplier);
            spawnRule.init();
            spawnRules.add(spawnRule);
            return this;
        }

        public Builder addNoSpawnRate() {
            spawnRules.add(new NoNaturalSpawnRate(world));
            return this;
        }

        public SpawnRateService build() {
            return new SpawnRateService(this);
        }
    }
}
