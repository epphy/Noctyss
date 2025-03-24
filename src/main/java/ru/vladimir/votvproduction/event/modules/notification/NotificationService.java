package ru.vladimir.votvproduction.event.modules.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.votvproduction.config.notification.Toast;
import ru.vladimir.votvproduction.event.modules.Module;

import java.util.ArrayList;
import java.util.List;

// TODO
//  Make sure to load all notification rules correctly:
//  All listeners are being registered;
//  All schedulers are being started,

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

    }

    @Override
    public void stop() {

    }

    @Getter
    @RequiredArgsConstructor
    public static class Builder {
        private final JavaPlugin plugin;
        private final PluginManager pluginManager;
        private final World world;
        private final List<NotificationRule> notificationRules = new ArrayList<>();

        public Builder addToastEndEvent(Toast endToast) {
            notificationRules.add(new ToastEndEvent(plugin, world, endToast));
            return this;
        }

        public NotificationService build() {
            return new NotificationService(this);
        }
    }
}
