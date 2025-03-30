package ru.vladimir.noctyss.event.modules.environment;

import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import ru.vladimir.noctyss.event.Controllable;

@RequiredArgsConstructor
final class EntityAIKiller implements EnvironmentModifier, Controllable {
    private final World world;

    @Override
    public void start() {
        for (final LivingEntity entity : world.getLivingEntities()) {
            entity.setAI(false);
        }
    }

    @Override
    public void stop() {
        for (final LivingEntity entity : world.getLivingEntities()) {
            entity.setAI(true);
        }
    }
}
