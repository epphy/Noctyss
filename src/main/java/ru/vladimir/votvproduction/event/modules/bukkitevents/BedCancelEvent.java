package ru.vladimir.votvproduction.event.modules.bukkitevents;

import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

@RequiredArgsConstructor
public class BedCancelEvent implements Listener {
    private final World world;

    @EventHandler
    private void on(PlayerBedEnterEvent event) {
        if (event.getBed().getWorld().equals(world)) {
            event.getPlayer().sendMessage();
            event.setCancelled(true);
        }
    }
}
