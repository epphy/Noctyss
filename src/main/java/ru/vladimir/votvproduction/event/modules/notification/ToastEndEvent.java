package ru.vladimir.votvproduction.event.modules.notification;

import eu.endercentral.crazy_advancements.advancement.ToastNotification;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.votvproduction.config.Toast;

@RequiredArgsConstructor
class ToastEndEvent implements NotificationRule {
    private final JavaPlugin plugin;
    private final World world;
    private final Toast endToast;

    @Override
    public void send() {
        final ToastNotification toastNotification = endToast.toastNotification();

        for (final Player player : world.getPlayers()) {
            Bukkit.getScheduler().runTask(plugin, () -> toastNotification.send(player));
        }
    }
}
