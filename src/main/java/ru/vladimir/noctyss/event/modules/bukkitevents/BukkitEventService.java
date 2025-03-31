package ru.vladimir.noctyss.event.modules.bukkitevents;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.event.modules.Module;
import ru.vladimir.noctyss.utility.LoggerUtility;
import ru.vladimir.noctyss.utility.TaskUtil;

import java.util.ArrayList;
import java.util.List;

public final class BukkitEventService implements Module {
    private final JavaPlugin plugin;
    private final PluginManager pluginManager;
    private final EventType eventType;
    private final World world;
    private final List<BukkitEvent> bukkitEvents;

    private BukkitEventService(Builder builder) {
        this.plugin = builder.getPlugin();
        this.pluginManager = builder.getPluginManager();
        this.eventType = builder.getEventType();
        this.world = builder.getWorld();
        this.bukkitEvents = builder.getBukkitEvents();
    }

    @Override
    public void start() {
        int started = 0;
        for (final BukkitEvent bukkitEvent : bukkitEvents) {
            TaskUtil.runTask(plugin, () ->
                    pluginManager.registerEvents(bukkitEvent, plugin));
            started++;
            LoggerUtility.info(this, "Started '%s' in '%s' for '%s'"
                    .formatted(bukkitEvent.getClass().getSimpleName(), world.getName(), eventType.name()));
        }
        LoggerUtility.info(this, "Started all '%d' in '%s' for '%s'"
                .formatted(started, world.getName(), eventType.name()));
    }

    @Override
    public void stop() {
        int stopped = 0;
        for (final BukkitEvent bukkitEvent : bukkitEvents) {
            TaskUtil.runTask(plugin, () ->
                    HandlerList.unregisterAll(bukkitEvent));
            stopped++;
            LoggerUtility.info(this, "Stopped '%s' in '%s' for '%s'"
                    .formatted(bukkitEvent.getClass().getSimpleName(), world.getName(), eventType.name()));
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
        private final List<BukkitEvent> bukkitEvents = new ArrayList<>();

        public Builder addBedCancelEvent(Component cannotSleep) {
            bukkitEvents.add(new BedCancelEvent(world, cannotSleep));
            return this;
        }

        public Builder addChunkRefresher() {
            bukkitEvents.add(new ChunkRefresher());
            return this;
        }

        public BukkitEventService build() {
            return new BukkitEventService(this);
        }
    }
}
