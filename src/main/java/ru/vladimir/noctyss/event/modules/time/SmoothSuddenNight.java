package ru.vladimir.noctyss.event.modules.time;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.event.Controllable;
import ru.vladimir.noctyss.event.EventManager;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.utility.GameTimeUtility;
import ru.vladimir.noctyss.utility.LoggerUtility;

@RequiredArgsConstructor
public class SmoothSuddenNight implements TimeModificationRule, Controllable, Listener {
    private static final long MIDNIGHT_TIME = 18000L;
    private final JavaPlugin plugin;
    private final EventManager eventManager;
    private final EventType eventType;
    private final World world;
    private final long nightLength;
    private final long frequency;
    private long originalWorldTime;
    private long elapsedTime;
    private int taskId = -1;

    @Override
    public void start() {
        originalWorldTime = world.getTime();
        world.setStorm(false);
        GameTimeUtility.setTimeDynamically(world, MIDNIGHT_TIME, frequency);
        taskId = Bukkit.getScheduler().runTaskTimerAsynchronously
                (plugin, this::processTime, frequency, frequency).getTaskId();
    }

    private void processTime() {
        elapsedTime += frequency;

        if (elapsedTime >= nightLength) {
            eventManager.stopEvent(world, eventType);
            return;
        }

        GameTimeUtility.setTime(world, MIDNIGHT_TIME);
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
            GameTimeUtility.setTimeDynamically(world, originalWorldTime, frequency);
        }
    }
}
