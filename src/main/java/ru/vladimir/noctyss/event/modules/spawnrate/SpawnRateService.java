package ru.vladimir.noctyss.event.modules.spawnrate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.event.modules.Module;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.ArrayList;
import java.util.List;

public class SpawnRateService implements Module {
    private final JavaPlugin plugin;
    private final PluginManager pluginManager;
    private final World world;
    private final List<SpawnRule> spawnRules;

    private SpawnRateService(Builder builder) {
        this.plugin = builder.getPlugin();
        this.pluginManager = builder.getPluginManager();
        this.world = builder.getWorld();
        this.spawnRules = builder.getSpawnRules();
    }

    @Override
    public void start() {
        int registered = 0;
        for (final SpawnRule spawnRule : spawnRules) {
            pluginManager.registerEvents(spawnRule, plugin);
            registered++;
            LoggerUtility.info(this, "Registered '%s' spawn rule in '%s'"
                    .formatted(spawnRule.getClass().getSimpleName(), world.getName()));
        }
        LoggerUtility.info(this, "Registered '%d' spawn rules in '%s'".formatted(registered, world.getName()));
    }

    @Override
    public void stop() {
        int unregistered = 0;
        for (final SpawnRule spawnRule : spawnRules) {
            HandlerList.unregisterAll(spawnRule);
            unregistered++;
            LoggerUtility.info(this, "Unregistered '%s' spawn rule in '%s'"
                    .formatted(spawnRule.getClass().getSimpleName(), world.getName()));
        }
        LoggerUtility.info(this, "Unregistered '%d' spawn rules in '%s'".formatted(unregistered, world.getName()));
    }

    @Getter
    @RequiredArgsConstructor
    public static class Builder {
        private final JavaPlugin plugin;
        private final PluginManager pluginManager;
        private final World world;
        private final List<SpawnRule> spawnRules = new ArrayList<>();

        public Builder addMonsterSpawnMultiplier(int multiplier) {
            MonsterSpawnMultiplier spawnRule = new MonsterSpawnMultiplier(world, multiplier);
            spawnRule.init();
            spawnRules.add(spawnRule);
            return this;
        }

        public SpawnRateService build() {
            return new SpawnRateService(this);
        }
    }
}
