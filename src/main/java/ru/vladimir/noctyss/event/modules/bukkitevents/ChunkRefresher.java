package ru.vladimir.noctyss.event.modules.bukkitevents;

import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

final class ChunkRefresher implements BukkitEvent {

    @EventHandler
    private void on(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (!action.isLeftClick() && !action.isRightClick()) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        Material blockMaterial = block.getType();
        if (blockMaterial == Material.AIR) return;

        Player player = event.getPlayer();
        World world = block.getWorld();
        Chunk chunk = block.getChunk();
        if (action.isRightClick()) {
            boolean refreshed = world.refreshChunk(chunk.getX(), chunk.getZ());
            player.sendActionBar(Component.text("Chunk has been refreshed: %b".formatted(refreshed)));
        } else if (action.isLeftClick()) {
            boolean unload = world.unloadChunk(chunk);
            world.loadChunk(chunk);
            player.sendActionBar(Component.text("Chunk has been reloaded: %b".formatted(unload)));
        }
    }
}
