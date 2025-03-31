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

        final var spawnReason = event.getSpawnReason();
        if (!isBlockedSpawnReason(spawnReason)) return;

        event.setCancelled(true);
    }

    private boolean isBlockedSpawnReason(CreatureSpawnEvent.SpawnReason spawnReason) {
        return spawnReason == CreatureSpawnEvent.SpawnReason.SPAWNER ||
               spawnReason == CreatureSpawnEvent.SpawnReason.NATURAL ||
               spawnReason == CreatureSpawnEvent.SpawnReason.VILLAGE_INVASION ||
               spawnReason == CreatureSpawnEvent.SpawnReason.REINFORCEMENTS;
    }
}
