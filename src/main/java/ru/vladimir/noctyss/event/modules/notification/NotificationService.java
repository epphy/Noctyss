package ru.vladimir.noctyss.event.modules.notification;

import eu.endercentral.crazy_advancements.advancement.ToastNotification;
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

public final class NotificationService implements Module {
    private final JavaPlugin plugin;
    private final PluginManager pluginManager;
    private final EventType eventType;
    private final World world;
    private final List<NotificationRule> notificationRules;

    private NotificationService(Builder builder) {
        this.plugin = builder.getPlugin();
        this.pluginManager = builder.pluginManager;
        this.eventType = builder.getEventType();
        this.world = builder.getWorld();
        this.notificationRules = builder.getNotificationRules();
    }

    @Override
    public void start() {
        int started = 0;

        for (final NotificationRule rule : notificationRules) {

            if (rule instanceof Controllable) {
                ((Controllable) rule).start();
            }

            if (rule instanceof Listener) {
                TaskUtil.runTask(plugin, () ->
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

        for (final NotificationRule rule : notificationRules) {

            if (rule instanceof Listener) {
                TaskUtil.runTask(plugin, () ->
                        HandlerList.unregisterAll((Listener) rule));
            }

            if (rule instanceof Controllable) {
                ((Controllable) rule).stop();
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
        private final List<NotificationRule> notificationRules = new ArrayList<>();

        public Builder addToastEndEvent(boolean oneTime, ToastNotification endToast) {
            final var rule = new ToastSenderOnEvent(eventType, world, oneTime, endToast);
            notificationRules.add(rule);
            return this;
        }

        public NotificationService build() {
            return new NotificationService(this);
        }
    }
}
