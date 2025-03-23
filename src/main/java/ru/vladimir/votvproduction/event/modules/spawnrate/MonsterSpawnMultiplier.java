package ru.vladimir.votvproduction.event.modules.spawnrate;

import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;

@RequiredArgsConstructor
final class MonsterSpawnMultiplier implements SpawnRule {
    private final World world;
    private final int multiplier;

    @EventHandler
    private void on(CreatureSpawnEvent event) {
        final Location eventLocation = event.getLocation();
        final World eventWorld = eventLocation.getWorld();
        if (!eventWorld.equals(world) || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM)
            return;

        final EntityType entityType = event.getEntityType();
        for (int i = 0; i < multiplier; i++) {
            eventWorld.spawnEntity(eventLocation, entityType, CreatureSpawnEvent.SpawnReason.CUSTOM);
        }
    }
}
