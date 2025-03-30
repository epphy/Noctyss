package ru.vladimir.noctyss.event.modules.notification;

import eu.endercentral.crazy_advancements.advancement.ToastNotification;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldEvent;
import ru.vladimir.noctyss.api.events.ICustomEvent;
import ru.vladimir.noctyss.api.events.nightmarenight.NightmareNightEndEvent;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.event.modules.notification.storage.PlayerNotificationService;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.*;

@RequiredArgsConstructor
final class ToastSenderOnEvent implements NotificationRule, Listener {
    private final PlayerNotificationService service;
    private final EventType eventType;
    private final ICustomEvent eventToListenTo;
    private final World world;
    private final boolean oneTime;
    private final ToastNotification endToast;

    @EventHandler
    public void on(WorldEvent event) {
        if (!event.getWorld().equals(world)) return;
        if (!(event.getClass() == eventToListenTo.getClass())) return;

        if (oneTime) sendNotificationAndStore();
        else sendNotification();
    }

    private void sendNotification() {
        for (final Player player : world.getPlayers()) {
            endToast.send(player);
        }
        LoggerUtility.info(this, "Notification sent");
    }

    private void sendNotificationAndStore() {
        final Set<UUID> excludedPlayerIds = getExcludedPlayerIds();
        final Set<UUID> newExcludedPlayerIds = new HashSet<>();

        for (final Player player : world.getPlayers()) {
            final UUID playerId = player.getUniqueId();
            if (excludedPlayerIds.contains(playerId)) continue;
            endToast.send(player);
            newExcludedPlayerIds.add(playerId);
        }

        storeNewExcludedPlayerIds(newExcludedPlayerIds);
        LoggerUtility.info(this, "Notification sent and new users were stored");
    }

    private Set<UUID> getExcludedPlayerIds() {
        return service.getExcludedPlayersFor(world, eventType, this.getClass().getSimpleName());
    }

    private void storeNewExcludedPlayerIds(Set<UUID> newExcludedPlayerIds) {
        service.addNewExcludedPlayerIds(world, eventType, this.getClass().getSimpleName(), newExcludedPlayerIds);
    }
}
