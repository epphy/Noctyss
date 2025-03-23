package ru.vladimir.votvproduction.event.modules.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.votvproduction.event.modules.Module;

import java.util.ArrayList;
import java.util.List;

public class NotificationService implements Module {
    private final JavaPlugin plugin;
    private final World world;
    private final List<NotificationRule> notificationRules;

    private NotificationService(Builder builder) {
        this.plugin = builder.getPlugin();
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
        private final World world;
        private final List<NotificationRule> notificationRules = new ArrayList<>();
    }
}
