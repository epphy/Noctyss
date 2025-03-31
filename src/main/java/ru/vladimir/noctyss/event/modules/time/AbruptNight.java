package ru.vladimir.noctyss.event.modules.time;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.event.Controllable;
import ru.vladimir.noctyss.event.EventManager;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.utility.GameTimeUtility;
import ru.vladimir.noctyss.utility.TaskUtil;

import java.util.Random;

@RequiredArgsConstructor
final class AbruptNight implements TimeModificationRule, Controllable, Listener {
    private static final long DELAY = 0L;
    private static final long MIDNIGHT_TIME = 18000L;
    private final JavaPlugin plugin;
    private final EventManager eventManager;
    private final EventType eventType;
    private final World world;
    private final long[] nightLengthRange;
    private final long frequency;
    private final Random random;
    private long originalWorldTime;
    private long nightLength;
    private long elapsedTime;
    private int taskId = -1;

    @Override
    public void start() {
        setNightLength();
        originalWorldTime = world.getTime();
        world.setStorm(false);
        taskId = Bukkit.getScheduler().runTaskTimer(
                plugin, this::processTime, DELAY, frequency).getTaskId();
    }

    private void setNightLength() {
        nightLength = random.nextLong(nightLengthRange[0], nightLengthRange[1]);
    }

    private void processTime() {
        elapsedTime += frequency;

        if (elapsedTime >= nightLength) {
            eventManager.stopEvent(world, eventType);
            return;
        }

        if (world.getTime() != MIDNIGHT_TIME) {
            setDoDaylightCycle(false);
            GameTimeUtility.setTime(world, MIDNIGHT_TIME);
        }
    }

    @EventHandler
    private void on(WeatherChangeEvent event) {
        if (!event.getWorld().equals(world)) return;
        event.setCancelled(true);
    }

    @Override
    public void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            setDoDaylightCycle(true);
            GameTimeUtility.setTime(world, originalWorldTime);
        }
    }

    private void setDoDaylightCycle(boolean value) {
        TaskUtil.runTask(plugin, () -> world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, value));
    }
}
