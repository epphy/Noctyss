package ru.vladimir.votvproduction.event.modules;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.votvproduction.utility.LoggerUtility;

import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
public final class SoundPlayer implements Module {
    private static final long DELAY = 0L;
    private final JavaPlugin plugin;
    private final World world;
    private final Random random;
    private final List<Sound> sounds;
    private final long frequency;
    private int taskId = -1;

    @Override
    public void start() {
        taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(
                plugin, this::playSound, DELAY, frequency).getTaskId();
        LoggerUtility.info(this, "Started scheduler for world '%s'.".formatted(world));
    }

    private void playSound() {
        final Sound sound = getSound();
        final float randomVolume = random.nextFloat(1.0f, 5.0f);
        final float randomPitch = random.nextFloat(1.0f, 5.0f);

        for (final Player player : world.getPlayers()) {
            player.playSound(player, sound, randomVolume, randomPitch);
        }
    }

    private Sound getSound() {
        final int randomIndex = random.nextInt(sounds.size() - 1);
        return sounds.get(randomIndex);
    }

    @Override
    public void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            LoggerUtility.info(this, "Stopped scheduler for %s".formatted(world));
        } else {
            LoggerUtility.info(this, "Cannot stop scheduler for %s".formatted(world));
        }
    }
}
