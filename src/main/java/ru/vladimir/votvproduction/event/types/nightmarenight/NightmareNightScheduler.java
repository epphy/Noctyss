package ru.vladimir.votvproduction.event.types.nightmarenight;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.votvproduction.config.NightmareNightConfig;
import ru.vladimir.votvproduction.event.types.EventScheduler;
import ru.vladimir.votvproduction.utility.LoggerUtility;

@RequiredArgsConstructor
public class NightmareNightScheduler implements EventScheduler {
    private static final long DELAY = 0L;
    private final JavaPlugin plugin;
    private final NightmareNightConfig config;
    private int taskId = -1;

    @Override
    public void start() {
        taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(
                plugin, this::processWorlds, DELAY, config.getCheckFrequency()).getTaskId();
    }

    private void processWorlds() {

    }

    @Override
    public void stop() {
        if (taskId == -1) {
            LoggerUtility.info(this, "Scheduler is not active");
            return;
        }
        Bukkit.getScheduler().cancelTask(taskId);
        LoggerUtility.info(this, "Stopped scheduler");
    }
}
