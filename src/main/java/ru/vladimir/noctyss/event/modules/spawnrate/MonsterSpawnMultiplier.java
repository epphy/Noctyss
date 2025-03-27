package ru.vladimir.noctyss.event.modules.spawnrate;

import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;

@RequiredArgsConstructor
final class MonsterSpawnMultiplier implements SpawnRule {
    private final World world;
    private final int multiplier;
    private int modifiedMultiplier;

    public void init() {
        cache();
    }

    @EventHandler
    private void on(CreatureSpawnEvent event) {
        final Location eventLocation = event.getLocation();
        final World eventWorld = eventLocation.getWorld();
        if (!eventWorld.equals(world) || event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL)
            return;

        final Entity entity = event.getEntity();
        if (!(entity instanceof Monster)) return;

        final EntityType entityType = entity.getType();
        for (int i = 0; i < modifiedMultiplier; i++) {
            eventWorld.spawnEntity(eventLocation, entityType, CreatureSpawnEvent.SpawnReason.CUSTOM);
        }
    }

    private void cache() {
        modifiedMultiplier = multiplier - 1;
    }
}
