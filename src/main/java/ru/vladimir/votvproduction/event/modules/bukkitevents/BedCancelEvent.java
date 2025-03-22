package ru.vladimir.votvproduction.event.modules.bukkitevents;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

@RequiredArgsConstructor
public class BedCancelEvent implements Listener {
    private final World world;
    private final Component cannotSleep;

    @EventHandler
    private void on(PlayerBedEnterEvent event) {
        if (event.getBed().getWorld().equals(world)) {
            event.getPlayer().sendMessage(cannotSleep);
            event.setCancelled(true);
        }
    }
}
