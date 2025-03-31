package ru.vladimir.noctyss.event.modules.notification.senders;

import eu.endercentral.crazy_advancements.advancement.ToastNotification;
import lombok.experimental.UtilityClass;
import org.bukkit.World;
import ru.vladimir.noctyss.event.EventType;

@UtilityClass
public class NotificationManager {
    private static ToastSender toastSender;

    public static void init() {
        load();
    }

    private static void load() {
        toastSender = new ToastSender();
    }

    public static void sendToast(EventType eventType, World world, boolean oneTime, String className, ToastNotification toast) {
        toastSender.send(eventType, world, oneTime, className, toast);
    }
}
