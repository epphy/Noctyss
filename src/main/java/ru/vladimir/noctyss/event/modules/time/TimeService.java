package ru.vladimir.noctyss.event.modules.time;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.event.Controllable;
import ru.vladimir.noctyss.event.EventManager;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.event.modules.Module;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class TimeService implements Module {
    private final List<TimeModificationRule> rules;
    private final JavaPlugin plugin;
    private final PluginManager pluginManager;
    private final World world;
    private final EventType eventType;

    private TimeService(Builder builder) {
        this.rules = builder.getRules();
        this.plugin = builder.getPlugin();
        this.pluginManager = builder.getPluginManager();
        this.world = builder.getWorld();
        this.eventType = builder.getEventType();
    }

    @Override
    public void start() {
        int started = 0;
        for (final TimeModificationRule rule : rules) {

            if (rule instanceof Controllable) {
                ((Controllable) rule).start();
            }

            if (rule instanceof Listener) {
                Bukkit.getScheduler().runTask(plugin, () ->
                        pluginManager.registerEvents((Listener) rule, plugin));
            }

            started++;
            LoggerUtility.info(this, "Started '%s' in '%s' for '%s'"
                    .formatted(rule.getClass().getSimpleName(), world.getName(), eventType.name()));
        }
        LoggerUtility.info(this, "Started all '%d' in '%s' for '%s'"
                .formatted(started, world.getName(), eventType.name()));
    }

    @Override
    public void stop() {
        int stopped = 0;
        for (final TimeModificationRule rule : rules) {

            if (rule instanceof Controllable) {
                ((Controllable) rule).stop();
            }

            if (rule instanceof Listener) {
                Bukkit.getScheduler().runTask(plugin, () ->
                        HandlerList.unregisterAll((Listener) rule));
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
        private final List<TimeModificationRule> rules = new ArrayList<>();
        private final JavaPlugin plugin;
        private final PluginManager pluginManager;
        private final EventManager eventManager;
        private final World world;
        private final EventType eventType;

        public Builder addMidnightLoopModifier(long frequency, long nightLength) {
            final var rule = new MidnightLoopModifier(
                    plugin, world, eventManager, eventType, frequency, nightLength);
            rules.add(rule);
            return this;
        }

        public Builder addAbruptNight(long frequency, long[] nightLength, Random random) {
            final var abruptNight = new AbruptNight(plugin, eventManager, eventType, world, nightLength, frequency, random);
            rules.add(abruptNight);
            return this;
        }

        public TimeService build() {
            return new TimeService(this);
        }
    }
}
