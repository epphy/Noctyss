package ru.vladimir.noctyss.event.modules.environment;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.event.Controllable;
import ru.vladimir.noctyss.utility.TaskUtil;

@RequiredArgsConstructor
final class EntityAIKiller implements EnvironmentModifier, Controllable {
    private static final long DELAY = 0L;
    private static final long FREQUENCY = 100L;
    private final JavaPlugin plugin;
    private final World world;
    private int taskId = -1;

    @Override
    public void start() {
         taskId = Bukkit.getScheduler().runTaskTimer(plugin, () ->
                updateEntitiesAI(false), DELAY, FREQUENCY).getTaskId();
    }

    @Override
    public void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            updateEntitiesAI(true);
        }
    }

    private void updateEntitiesAI(boolean value) {
        TaskUtil.getInstance().runTask(plugin, () -> {
            for (final LivingEntity entity : world.getLivingEntities()) {
                if (entity.isDead()) continue; // Skip dead entities
                entity.setAI(value);
            }
        });
    }
}
