package ru.vladimir.noctyss.event.modules.notification.senders;

import eu.endercentral.crazy_advancements.advancement.ToastNotification;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.event.modules.notification.storage.PlayerNotificationService;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
final class ToastSender {
    private final PlayerNotificationService notificationService;

    void send(EventType eventType, World world, boolean oneTime, ToastNotification toast) {
        if (oneTime) sendToPlayersOneTime(world, eventType, toast);
        else sendToPlayers(world, toast);
    }

    private void sendToPlayers(World world, ToastNotification toast) {
        world.getPlayers().forEach(toast::send);
    }

    private void sendToPlayersOneTime(World world, EventType eventType, ToastNotification toast) {
        final Set<UUID> excludedPlayerIds = getExcludedPlayerIds(world, eventType);
        final Set<UUID> newExcludedPlayerIds = new HashSet<>();

        for (final Player player : world.getPlayers()) {
            final UUID playerId = player.getUniqueId();
            if (excludedPlayerIds.contains(playerId)) continue;

            toast.send(player);
            newExcludedPlayerIds.add(playerId);
        }

        storeNewExcludedPlayerIds(world, eventType, newExcludedPlayerIds);
    }

    private Set<UUID> getExcludedPlayerIds(World world, EventType eventType) {
        return notificationService.getExcludedPlayersFor(
                world, eventType, getClass().getSimpleName());
    }

    private void storeNewExcludedPlayerIds(World world, EventType eventType, Set<UUID> playerIds) {
        notificationService.addNewExcludedPlayerIds(
                world, eventType, getClass().getSimpleName(), playerIds);
    }
}
