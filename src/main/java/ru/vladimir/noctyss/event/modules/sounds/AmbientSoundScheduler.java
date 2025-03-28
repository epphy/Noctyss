package ru.vladimir.noctyss.event.modules.sounds;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.event.Controllable;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
final class AmbientSoundScheduler implements SoundManager, Controllable {
    private final JavaPlugin plugin;
    private final long[] delayRange;
    private final long[] frequencyRange;
    private final World world;
    private final EventType eventType;
    private final List<Sound> sounds;
    private final Random random;
    private long delay;
    private long frequency;
    private int taskId = -1;

    @Override
    public void start() {
        setSchedulerParams();
        taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(
                plugin, this::playAmbient, delay, frequency).getTaskId();
    }

    private void setSchedulerParams() {
        delay = getDelay();
        frequency = getFrequency();
    }

    private long getDelay() {
        return random.nextLong(delayRange[0], delayRange[1]);
    }

    public long getFrequency() {
        return random.nextLong(frequencyRange[0], frequencyRange[1]);
    }

    private void playAmbient() {
        final Sound sound = getSound();
        if (sound == null) {
            LoggerUtility.warn(this, "Failed to play an ambience for players in '%s' for '%s'"
                    .formatted(world.getName(), eventType.name()));
            return;
        }

        LoggerUtility.info(this, "Scheduling sound '%s'".formatted(sound));
        for (final Player player : world.getPlayers()) {
            Bukkit.getScheduler().runTask(plugin, () ->
                    player.playSound(player, sound, 1.0f, 1.0f));
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
            stopAmbient();
        }
    }

    private void stopAmbient() {
        for (final Player player : world.getPlayers()) {
            for (final Sound sound : sounds) {
                Bukkit.getScheduler().runTask(plugin, () -> player.stopSound(sound));
            }
        }
    }
}
