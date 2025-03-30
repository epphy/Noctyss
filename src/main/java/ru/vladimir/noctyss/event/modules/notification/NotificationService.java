package ru.vladimir.noctyss.event.modules.notification;

import eu.endercentral.crazy_advancements.advancement.ToastNotification;
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
        int registered = 0;
        for (final NotificationRule rule : notificationRules) {
            if (rule instanceof Listener) {
                pluginManager.registerEvents((Listener) rule, plugin);
            }

            if (rule instanceof Controllable) {
                ((Controllable) rule).start();
            }

            registered++;
            LoggerUtility.info(this, "Registered '%s' notification rule in '%s'"
                    .formatted(rule.getClass().getSimpleName(), world.getName()));
        }
        LoggerUtility.info(this, "All notification rules '%d' registered in '%s'"
                .formatted(registered, world.getName()));
    }

    @Override
    public void stop() {
        int unregistered = 0;
        for (final NotificationRule rule : notificationRules) {
            if (rule instanceof Listener) {
                HandlerList.unregisterAll((Listener) rule);
            }

            if (rule instanceof Controllable) {
                ((Controllable) rule).stop();
            }

            unregistered++;
            LoggerUtility.info(this, "Unregistered '%s' notification rule in '%s'"
                    .formatted(rule.getClass().getSimpleName(), world.getName()));
        }
        LoggerUtility.info(this, "All notification rules '%d' unregistered in world %s"
                .formatted(unregistered, world.getName()));
    }

    @Getter
    @RequiredArgsConstructor
    public static class Builder {
        private final JavaPlugin plugin;
        private final PluginManager pluginManager;
        private final EventType eventType;
        private final World world;
        private final List<NotificationRule> notificationRules = new ArrayList<>();

        public Builder addToastEndEvent(boolean oneTime, ToastNotification endToast) {
            notificationRules.add(new ToastSenderOnEvent(eventType, world, oneTime, endToast));
            return this;
        }

        public NotificationService build() {
            return new NotificationService(this);
        }
    }
}
