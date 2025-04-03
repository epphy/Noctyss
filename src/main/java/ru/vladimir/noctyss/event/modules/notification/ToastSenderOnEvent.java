package ru.vladimir.noctyss.event.modules.notification;

import eu.endercentral.crazy_advancements.advancement.ToastNotification;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.vladimir.noctyss.api.events.suddennight.SuddenNightEndEvent;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.event.modules.notification.senders.NotificationManager;

@RequiredArgsConstructor
final class ToastSenderOnEvent implements NotificationRule, Listener {
    private final EventType eventType;
    private final World world;
    private final boolean oneTime;
    private final ToastNotification endToast;

    @EventHandler
    public void on(SuddenNightEndEvent event) {
        if (!event.getWorld().equals(world)) return;
        NotificationManager.getInstance().sendToast(eventType, world, oneTime, getClass().getSimpleName(), endToast);
    }
}
