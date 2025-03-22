package ru.vladimir.votvproduction.event.modules.bukkitevents;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.votvproduction.event.modules.Module;
import ru.vladimir.votvproduction.utility.LoggerUtility;

import java.util.ArrayList;
import java.util.List;

public class BukkitEventManager implements Module {
    private final JavaPlugin plugin;
    private final PluginManager pluginManager;
    private final World world;
    private final List<BukkitEvent> bukkitEvents;

    private BukkitEventManager(Builder builder) {
        this.plugin = builder.getPlugin();
        this.pluginManager = builder.getPluginManager();
        this.world = builder.getWorld();
        this.bukkitEvents = builder.getBukkitEvents();
    }

    @Override
    public void start() {
        int registeredEventsNumber = 0;
        for (final BukkitEvent bukkitEvent : bukkitEvents) {
            pluginManager.registerEvents(bukkitEvent, plugin);
            registeredEventsNumber++;
            LoggerUtility.info(this, "Registered '%s' bukkit event in '%s'"
                    .formatted(bukkitEvent.getClass().getSimpleName(), world));
        }
        LoggerUtility.info(this, "All events '%d' registered in '%s'".formatted(registeredEventsNumber, world));
    }

    @Override
    public void stop() {
        int unregisteredEventsNumber = 0;
        for (final BukkitEvent bukkitEvent : bukkitEvents) {
            HandlerList.unregisterAll(bukkitEvent);
            unregisteredEventsNumber++;
            LoggerUtility.info(this, "Unregistered '%s' bukkit event in '%s'"
                    .formatted(bukkitEvent.getClass().getSimpleName(), world));
        }
        LoggerUtility.info(this, "All events '%d' unregistered in '%s'".formatted(unregisteredEventsNumber, world));
    }

    @Getter
    @RequiredArgsConstructor
    public static class Builder {
        private final JavaPlugin plugin;
        private final PluginManager pluginManager;
        private final World world;
        private final List<BukkitEvent> bukkitEvents = new ArrayList<>();

        public Builder addBedCancelEvent(Component cannotSleep) {
            bukkitEvents.add(new BedCancelEvent(world, cannotSleep));
            return this;
        }

        public BukkitEventManager build() {
            return new BukkitEventManager(this);
        }
    }
}
