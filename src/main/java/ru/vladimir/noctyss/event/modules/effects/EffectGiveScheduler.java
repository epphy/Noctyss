package ru.vladimir.noctyss.event.modules.effects;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.vladimir.noctyss.event.Controllable;
import ru.vladimir.noctyss.utility.TaskUtil;

import java.util.List;

@RequiredArgsConstructor
final class EffectGiveScheduler implements EffectManager, Controllable {
    private static final long DELAY = 0L;
    private final JavaPlugin plugin;
    private final World world;
    private final List<PotionEffect> effects;
    private final long frequency;
    private int taskId = -1;

    @Override
    public void start() {
        taskId = Bukkit.getScheduler().runTaskTimer(
                plugin, this::giveEffects, DELAY, frequency).getTaskId();
    }

    private void giveEffects() {
        for (final Player player : world.getPlayers()) {
            for (final PotionEffect effect : effects) {
                TaskUtil.getInstance().runTask(plugin, () -> player.addPotionEffect(effect));
            }
        }
    }

    @Override
    public void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            takeAwayEffects();
        }
    }

    private void takeAwayEffects() {
        final List<PotionEffectType> effectTypes = effects.stream()
                .map(PotionEffect::getType)
                .toList();

        for (final Player player : world.getPlayers()) {
            for (final PotionEffectType effectType : effectTypes) {
                TaskUtil.getInstance().runTask(plugin, () -> player.removePotionEffect(effectType));
            }
        }
    }
}
