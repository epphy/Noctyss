package ru.vladimir.noctyss.event.modules.bukkitevents;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

final class CustomLightLevelSetter implements BukkitEvent {

    @EventHandler
    private void on(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (!action.isLeftClick() && action.isRightClick()) return;

        Block block = event.getClickedBlock();
        if (block == null || block.getType() == Material.AIR) return;

        Player player = event.getPlayer();

        if (action.isRightClick()) {
            byte a = block.getLightFromBlocks();
            byte b = block.getLightFromSky();
            byte c = block.getLightLevel();
            player.sendMessage("Blocks: %d, Sky: %d, Level: %d"
                    .formatted(a, b, c));
        }
    }
}
