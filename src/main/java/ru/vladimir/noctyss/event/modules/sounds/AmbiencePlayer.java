package ru.vladimir.noctyss.event.modules.sounds;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.event.Controllable;

import java.util.List;
import java.util.Random;
import java.util.Set;

@RequiredArgsConstructor
final class AmbiencePlayer implements SoundManager, Controllable {
    private final JavaPlugin plugin;
    private final long delay;
    private final long frequency;
    private final World world;
    private final List<Sound> sounds;
    private final Random random;
    private int taskId = -1;

    @Override
    public void start() {
        taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(
                plugin, this::playAmbience, delay, frequency).getTaskId();
    }

    private void playAmbience() {
        final Sound sound = getSound();
        if (sound == null) {

        }

    }

    private Sound getSound() {
        if (sounds.size() <= 1) return sounds.getFirst();
        final int randomIndex = random.nextInt(sounds.size() - 1);
        return sounds.get(randomIndex);
    }

    @Override
    public void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            stopAmbience();
        }
    }

    private void stopAmbience() {
        for (final Player player : world.getPlayers()) {
            for (final Sound sound : sounds) {
                Bukkit.getScheduler().runTask(plugin, () -> player.stopSound(sound));
            }
        }
    }
}
