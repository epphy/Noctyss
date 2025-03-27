package ru.vladimir.noctyss.event.modules.effects;

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

public class EffectService implements Module {
    private final JavaPlugin plugin;
    private final PluginManager pluginManager;
    private final World world;
    private final EventType eventType;
    private final List<EffectManager> effectManagers;

    private EffectService(Builder builder) {
        this.plugin = builder.getPlugin();
        this.pluginManager = builder.getPluginManager();
        this.world = builder.getWorld();
        this.eventType = builder.getEventType();
        this.effectManagers = builder.getEffectManagers();
    }

    @Override
    public void start() {
        int started = 0;
        for (final EffectManager effectManager : effectManagers) {

            if (effectManager instanceof Controllable) {
                ((Controllable) effectManager).start();
            }

            if (effectManager instanceof Listener) {
                pluginManager.registerEvents((Listener) effectManager, plugin);
            }

            started++;
            LoggerUtility.info(this, "Started '%s' in '%s' for '%s'"
                    .formatted(effectManager.getClass().getSimpleName(), world.getName(), eventType.name()));
        }

        LoggerUtility.info(this, "Started '%d' effect managers in '%s' for '%s'"
                .formatted(started, world.getName(), eventType.name()));
    }

    @Override
    public void stop() {
        int stopped = 0;
        for (final EffectManager effectManager : effectManagers) {

            if (effectManager instanceof Controllable) {
                ((Controllable) effectManager).stop();
            }

            if (effectManager instanceof Listener) {
                HandlerList.unregisterAll((Listener) effectManager);
            }

            stopped++;
            LoggerUtility.info(this, "Stopped '%s' in '%s' for '%s'"
                    .formatted(effectManager.getClass().getSimpleName(), world.getName(), eventType.name()));
        }

        LoggerUtility.info(this, "Stopped '%d' effect managers in '%s' for '%s'"
                .formatted(stopped, world.getName(), eventType.name()));
    }

    @Getter
    @RequiredArgsConstructor
    public static class Builder {
        private final JavaPlugin plugin;
        private final PluginManager pluginManager;
        private final World world;
        private final EventType eventType;
        private final List<EffectManager> effectManagers = new ArrayList<>();

        public Builder addDarknessAtStartProvider(int duration) {
            final DarknessAtStartProvider effectManager = new DarknessAtStartProvider(plugin, world, duration);
            return this;
        }

        public EffectService build() {
            return new EffectService(this);
        }
    }
}
