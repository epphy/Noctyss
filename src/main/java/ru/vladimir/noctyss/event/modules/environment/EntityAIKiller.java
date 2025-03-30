package ru.vladimir.noctyss.event.modules.environment;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.event.Controllable;

@RequiredArgsConstructor
final class EntityAIKiller implements EnvironmentModifier, Controllable {
    private final JavaPlugin plugin;
    private final World world;

    @Override
    public void start() {
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (final LivingEntity entity : world.getLivingEntities()) {
                entity.setAI(false);
            }
        });
    }

    @Override
    public void stop() {
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (final LivingEntity entity : world.getLivingEntities()) {
                entity.setAI(true);
            }
        });
    }
}
