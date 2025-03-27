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
public class AbruptNight implements TimeModificationRule, Controllable {
    private static final long MIDNIGHT_TIME = 18000L;
    private final JavaPlugin plugin;
    private final EventManager eventManager;
    private final EventType eventType;
    private final World world;
    private final long nightLength;
    private final long frequency;
    private long originalTime;
    private long elapsedTime;
    private int taskId = -1;

    @Override
    public void start() {
        originalTime = world.getTime();
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

    @Override
    public void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            GameTimeUtility.setTime(world, originalTime);
        }
    }
}
