package ru.vladimir.votvproduction.event.modules;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.vladimir.votvproduction.utility.LoggerUtility;

import java.util.List;

@RequiredArgsConstructor
public final class EffectGiver implements Module {
    private static final long DELAY = 0L;
    private final JavaPlugin plugin;
    private final World world;
    private final List<PotionEffect> effects;
    private final long frequency;
    private int taskId = -1;

    @Override
    public void start() {
        taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(
                plugin, this::giveEffects, DELAY, frequency).getTaskId();
        LoggerUtility.info(this, "Started scheduler for world %s".formatted(world.getName()));
    }

    private void giveEffects() {
        for (final Player player : world.getPlayers()) {
            for (final PotionEffect effect : effects) {
                Bukkit.getScheduler().runTask(plugin, () -> player.addPotionEffect(effect));
            }
        }
    }

    @Override
    public void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            takeAwayEffects();
            LoggerUtility.info(this, "Stopped scheduler for %s".formatted(world.getName()));
        } else {
            LoggerUtility.info(this, "Cannot stop scheduler for %s".formatted(world.getName()));
        }
    }

    private void takeAwayEffects() {
        final List<PotionEffectType> effectTypes = effects.stream()
                .map(PotionEffect::getType)
                .toList();

        for (final Player player : world.getPlayers()) {
            for (final PotionEffectType effectType : effectTypes) {
                Bukkit.getScheduler().runTask(plugin, () -> player.removePotionEffect(effectType));
            }
        }
    }
}
