package ru.vladimir.votvproduction.event.modules.time;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.votvproduction.event.modules.Module;
import ru.vladimir.votvproduction.utility.GameTimeUtility;
import ru.vladimir.votvproduction.utility.LoggerUtility;

@RequiredArgsConstructor
public class MidnightLoopModifier implements Module {
    private static final long MORNING_TICKS_TIME = 0L;
    private static final long MIDNIGHT_TICKS_TIME = 18000L;
    private final JavaPlugin plugin;
    private final World world;
    private final long frequency;
    private final long nightLength;
    private NightState nightState = NightState.START;
    private long elapsedTime = 0;
    private int taskId = -1;

    private enum NightState {
        START, PAUSE, FINAL
    }

    @Override
    public void start() {
        taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(
                plugin, this::processTime, frequency, frequency).getTaskId();
        LoggerUtility.info(this, "Started scheduler for world '%s'".formatted(world));
    }

    private void processTime() {
        elapsedTime += frequency;

        if (GameTimeUtility.isMidnight(world) && nightState == NightState.START) {
            nightState = NightState.PAUSE;
        }

        if ((nightLength - elapsedTime) <= 6000L) {
            nightState = NightState.FINAL;
            GameTimeUtility.setTime(world, (nightLength - elapsedTime));
        }

        if (nightState == NightState.PAUSE) {
            GameTimeUtility.setTime(world, MIDNIGHT_TICKS_TIME);
        }
    }

    @Override
    public void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            GameTimeUtility.setTime(world, MORNING_TICKS_TIME);
            LoggerUtility.info(this, "Scheduler stopped for world '%s'".formatted(world));
        } else {
            LoggerUtility.warn(this, "Failed to stop scheduler for world '%s'".formatted(world));
        }
    }
}
