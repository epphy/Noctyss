package ru.vladimir.noctyss.event.modules.time;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.event.Controllable;
import ru.vladimir.noctyss.event.EventManager;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.utility.GameTimeUtility;

@RequiredArgsConstructor
final class MidnightLoopModifier implements TimeModificationRule, Controllable {
    private static final long MORNING_TICKS_TIME = 0L;
    private static final long NIGHT_START_TIME = 13000L;
    private static final long MIDNIGHT_TICKS_TIME = 18000L;
    private static final long FULL_DAY_TICKS_TIME = 24000L;
    private final JavaPlugin plugin;
    private final World world;
    private final EventManager eventManager;
    private final EventType eventType;
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
        GameTimeUtility.setTime(plugin, world, NIGHT_START_TIME);
        taskId = Bukkit.getScheduler().runTaskTimer(
                plugin, this::processTime, frequency, frequency).getTaskId();
    }

    private void processTime() {
        elapsedTime += frequency;

        if (GameTimeUtility.isMidnight(world) && nightState == NightState.START) {
            nightState = NightState.PAUSE;
        }

        if (elapsedTime >= nightLength && nightState == NightState.FINAL) {
            eventManager.stopEvent(world, eventType);
            return;
        }

        if ((nightLength - elapsedTime) <= 6000L && nightState != NightState.FINAL) {
            nightState = NightState.FINAL;
            GameTimeUtility.setTime(plugin, world, (FULL_DAY_TICKS_TIME - (nightLength - elapsedTime)));
            return;
        }

        if (nightState == NightState.PAUSE) {
            GameTimeUtility.setTime(plugin, world, MIDNIGHT_TICKS_TIME);
        }
    }

    @Override
    public void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            GameTimeUtility.setTime(plugin, world, MORNING_TICKS_TIME);
        }
    }
}
