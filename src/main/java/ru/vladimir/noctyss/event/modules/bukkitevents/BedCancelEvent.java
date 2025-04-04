package ru.vladimir.noctyss.event.modules.bukkitevents;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBedEnterEvent;
import ru.vladimir.noctyss.utility.MessageUtil;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class BedCancelEvent implements BukkitEvent {
    private final World world;
    private final Component cannotSleep;

    @EventHandler
    private void on(PlayerBedEnterEvent event) {
        if (event.getBed().getWorld().equals(world)) {
            MessageUtil.sendMessage(event.getPlayer(), cannotSleep);
            event.setCancelled(true);
        }
    }
}
