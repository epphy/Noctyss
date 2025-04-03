package ru.vladimir.noctyss.event.modules.notification.senders;

import eu.endercentral.crazy_advancements.advancement.ToastNotification;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.event.EventType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NotificationManager {
    private static NotificationManager instance;
    private ToastSender toastSender;

    public static NotificationManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("NotificationManager hasn't been initialized yet. Call init method first");
        }
        return instance;
    }

    public static void init(@NonNull JavaPlugin plugin) {
        instance = new NotificationManager();
        instance.load(plugin);
    }

    public static void unload() {
        instance.toastSender = null;
        instance = null;
    }

    private void load(@NonNull JavaPlugin plugin) {
        instance.toastSender = new ToastSender(plugin);
    }

    public void sendToast(EventType eventType, World world, boolean oneTime, String className, ToastNotification toast) {
        toastSender.send(eventType, world, oneTime, className, toast);
    }
}
