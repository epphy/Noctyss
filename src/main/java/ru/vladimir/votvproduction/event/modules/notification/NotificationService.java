package ru.vladimir.votvproduction.event.modules.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.votvproduction.config.notification.Toast;
import ru.vladimir.votvproduction.event.Controllable;
import ru.vladimir.votvproduction.event.EventType;
import ru.vladimir.votvproduction.event.modules.Module;
import ru.vladimir.votvproduction.event.modules.notification.storage.PlayerNotificationService;
import ru.vladimir.votvproduction.utility.LoggerUtility;

import java.util.ArrayList;
import java.util.List;

public class NotificationService implements Module {
    private final JavaPlugin plugin;
    private final PluginManager pluginManager;
    private final World world;
    private final List<NotificationRule> notificationRules;

    private NotificationService(Builder builder) {
        this.plugin = builder.getPlugin();
        this.pluginManager = builder.pluginManager;
        this.world = builder.getWorld();
        this.notificationRules = builder.getNotificationRules();
    }

    @Override
    public void start() {
        for (final NotificationRule rule : notificationRules) {
            if (rule instanceof Listener) {
                pluginManager.registerEvents((Listener) rule, plugin);
            }

            if (rule instanceof Controllable) {
                ((Controllable) rule).start();
            }
        }
        LoggerUtility.info(this, "All notification rules have been loaded for world %s"
                .formatted(world));
    }

    @Override
    public void stop() {
        for (final NotificationRule rule : notificationRules) {
            if (rule instanceof Listener) {
                HandlerList.unregisterAll((Listener) rule);
            }

            if (rule instanceof Controllable) {
                ((Controllable) rule).stop();
            }
        }
        LoggerUtility.info(this, "All notification rules have been unloaded for world %s"
                .formatted(world));
    }

    @Getter
    @RequiredArgsConstructor
    public static class Builder {
        private final JavaPlugin plugin;
        private final PluginManager pluginManager;
        private final PlayerNotificationService service;
        private final EventType eventType;
        private final World world;
        private final List<NotificationRule> notificationRules = new ArrayList<>();

        public Builder addToastEndEvent(boolean oneTime, Toast endToast) {
            notificationRules.add(new ToastEndEvent(plugin, service, eventType, world, oneTime, endToast));
            return this;
        }

        public NotificationService build() {
            return new NotificationService(this);
        }
    }
}
