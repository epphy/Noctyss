package ru.vladimir.noctyss.event.modules.sounds;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import ru.vladimir.noctyss.event.Controllable;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.utility.LoggerUtility;
import ru.vladimir.noctyss.utility.TaskUtil;

import java.util.Arrays;
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
        taskId = Bukkit.getScheduler().runTaskTimer(
                plugin, this::playAmbient, delay, frequency).getTaskId();
    }

    private void setSchedulerParams() {
        delay = random.nextLong(delayRange[0], delayRange[1]);
        frequency = random.nextLong(frequencyRange[0], frequencyRange[1]);
    }

    private void playAmbient() {
        final Sound sound = getSound();
        if (sound == null) {
            LoggerUtility.warn(this, "Failed to play an ambient in '%s' for '%s' because sound is null"
                    .formatted(world.getName(), eventType.name()));
            return;
        }

        for (final Player player : world.getPlayers()) {
            TaskUtil.runTask(plugin, () ->
                    player.playSound(player, sound, 1.0f, 1.0f));
        }
    }

    @Nullable
    private Sound getSound() {
        if (sounds.isEmpty()) return null;
        if (sounds.size() == 1) return sounds.getFirst();
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
                TaskUtil.runTask(plugin, () -> player.stopSound(sound));
                plugin.getServer().getScheduler();
            }
        }
    }

    @Override
    public String toString() {
        return "AmbientSoundScheduler{" +
                "taskId=" + taskId +
                ", frequency=" + frequency +
                ", delay=" + delay +
                ", sounds=" + sounds +
                ", world=" + world.getName() +
                ", frequencyRange=" + Arrays.toString(frequencyRange) +
                ", delayRange=" + Arrays.toString(delayRange) +
                ", eventType=" + eventType.name() +
                '}';
    }
}
