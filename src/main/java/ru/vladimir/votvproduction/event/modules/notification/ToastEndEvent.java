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

// TODO:
//  When is called, make sure to process one-time (aka store all players uuids via the
//  Map<World, Map<EventType, Map<NotificationRule, UUID>>> map, where world is the event
//  world, event type is the event type, notification rule is this, and uuid is player's id.

@RequiredArgsConstructor
final class ToastEndEvent implements NotificationRule, Listener {
    private final JavaPlugin plugin;
    private final World world;
    private final Toast endToast;

    @EventHandler
    public void on(NightmareNightEndEvent event) {
        final ToastNotification toast = endToast.toastNotification();
        for (final Player player : event.getWorld().getPlayers()) {
            toast.send(player);
        }
    }
}
