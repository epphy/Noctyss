package ru.vladimir.votvproduction.event.modules.notification;

import eu.endercentral.crazy_advancements.advancement.ToastNotification;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.votvproduction.config.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
class ToastEndEvent implements NotificationRule {
    private final JavaPlugin plugin;
    private final World world;
    private final Toast endToast;

    @Override
    public void send() {
        final ToastNotification toastNotification = endToast.toastNotification();
        final List<UUID> playerIds = new ArrayList<>();
        for (final Player player : world.getPlayers()) {
            Bukkit.getScheduler().runTask(plugin, () -> toastNotification.send(player));
            playerIds.add(player.getUniqueId());
        }

    }
}
