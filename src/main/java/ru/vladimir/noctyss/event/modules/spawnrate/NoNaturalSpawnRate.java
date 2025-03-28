package ru.vladimir.noctyss.event.modules.spawnrate;

import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

@RequiredArgsConstructor
final class NoNaturalSpawnRate implements SpawnRule, Listener {
    private final World world;

    @EventHandler
    private void on(CreatureSpawnEvent event) {
        if (!event.getEntity().getWorld().equals(world)) return;

        final CreatureSpawnEvent.SpawnReason spawnReason = event.getSpawnReason();
        if (spawnReason != CreatureSpawnEvent.SpawnReason.SPAWNER &&
            spawnReason != CreatureSpawnEvent.SpawnReason.NATURAL) return;
        event.setCancelled(true);
    }
}
