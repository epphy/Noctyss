package ru.vladimir.votvproduction.event.modules.notification;

import eu.endercentral.crazy_advancements.advancement.ToastNotification;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.votvproduction.api.events.nightmarenight.NightmareNightEndEvent;
import ru.vladimir.votvproduction.config.notification.Toast;
import ru.vladimir.votvproduction.event.EventType;
import ru.vladimir.votvproduction.event.modules.notification.storage.PlayerNotificationService;
import ru.vladimir.votvproduction.utility.LoggerUtility;

import java.util.*;

// TODO:
//  When is called, make sure to process one-time (aka store all players uuids via the
//  Map<World, Map<EventType, Map<NotificationRule, UUID>>> map, where world is the event
//  world, event type is the event type, notification rule is this, and uuid is player's id.

@RequiredArgsConstructor
final class ToastEndEvent implements NotificationRule, Listener {
    private final JavaPlugin plugin;
    private final PlayerNotificationService service;
    private final EventType eventType;
    private final World world;
    private final boolean oneTime;
    private final Toast endToast;

    @EventHandler
    public void on(NightmareNightEndEvent event) {
        if (!event.getWorld().equals(world)) return;

        if (oneTime) sendNotificationAndStore();
        else sendNotification();
    }

    private void sendNotification() {
        final ToastNotification toast = endToast.toastNotification();

        for (final Player player : world.getPlayers()) {
            toast.send(player);
        }
        LoggerUtility.info(this, "Notification sent");
    }

    private void sendNotificationAndStore() {
        final ToastNotification toast = endToast.toastNotification();

        final Set<UUID> excludedPlayerIds = getExcludedPlayerIds();
        final Set<UUID> newExcludedPlayerIds = new HashSet<>();

        for (final Player player : world.getPlayers()) {
            final UUID playerId = player.getUniqueId();
            if (excludedPlayerIds.contains(playerId)) continue;
            toast.send(player);
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
