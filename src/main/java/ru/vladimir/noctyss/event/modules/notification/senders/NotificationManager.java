package ru.vladimir.noctyss.event.modules.notification.senders;

import eu.endercentral.crazy_advancements.advancement.ToastNotification;
import lombok.experimental.UtilityClass;
import org.bukkit.World;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.event.modules.notification.storage.PlayerNotificationService;
import ru.vladimir.noctyss.utility.LoggerUtility;

@UtilityClass
public class NotificationManager {
    private static PlayerNotificationService notificationService;
    private static ToastSender toastSender;

    public static void init(PlayerNotificationService notificationService) {
        if (NotificationManager.notificationService == null) {
            NotificationManager.notificationService = notificationService;
            load();
            LoggerUtility.info("NotificationManager", "initialised");
        } else {
            LoggerUtility.info("NotificationManager", "Already initialised");
        }
    }

    private static void load() {
        toastSender = new ToastSender(notificationService);
    }

    public static void sendToast(EventType eventType, World world, boolean oneTime, ToastNotification toast) {
        toastSender.send(eventType, world, oneTime, toast);
    }
}
