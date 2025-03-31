package ru.vladimir.noctyss.event.modules.spawnrate;

import lombok.AccessLevel;
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
import ru.vladimir.noctyss.utility.TaskUtil;

import java.util.ArrayList;
import java.util.List;

public final class SpawnRateService implements Module {
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
        int started = 0;
        for (final SpawnRule spawnRule : spawnRules) {

            if (spawnRule instanceof Controllable) {
                ((Controllable) spawnRule).start();
            }

            if (spawnRule instanceof Listener) {
                TaskUtil.runTask(plugin, () -> pluginManager.registerEvents((Listener) spawnRule, plugin));
            }

            started++;
            LoggerUtility.info(this, "Started '%s' in '%s' for '%s'"
                    .formatted(spawnRule.getClass().getSimpleName(), world.getName(), eventType.name()));
        }
        LoggerUtility.info(this, "Started all '%d' in '%s' for '%s'"
                .formatted(started, world.getName(), eventType.name()));
    }

    @Override
    public void stop() {
        int stopped = 0;
        for (final SpawnRule rule : spawnRules) {

            if (rule instanceof Controllable) {
                ((Controllable) rule).stop();
            }

            if (rule instanceof Listener) {
                TaskUtil.runTask(plugin, () -> HandlerList.unregisterAll((Listener) rule));
            }

            stopped++;
            LoggerUtility.info(this, "Stopped '%s' in '%s' for '%s'"
                    .formatted(rule.getClass().getSimpleName(), world.getName(), eventType.name()));
        }
        LoggerUtility.info(this, "Stopped all '%d' in '%s' for '%s'"
                .formatted(stopped, world.getName(), eventType.name()));
    }

    @Getter(AccessLevel.PRIVATE)
    @RequiredArgsConstructor
    public static class Builder {
        private final JavaPlugin plugin;
        private final PluginManager pluginManager;
        private final EventType eventType;
        private final World world;
        private final List<SpawnRule> spawnRules = new ArrayList<>();

        public Builder addMonsterSpawnMultiplier(int multiplier) {
            final var spawnRule = new MonsterSpawnMultiplier(world, multiplier);
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
