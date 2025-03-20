package ru.vladimir.votvproduction.event.types.nightmarenight;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.votvproduction.config.NightmareNightConfig;
import ru.vladimir.votvproduction.event.types.EventScheduler;
import ru.vladimir.votvproduction.utility.GameTimeUtility;
import ru.vladimir.votvproduction.utility.LoggerUtility;

import java.util.List;
import java.util.Random;
import java.util.Set;

@RequiredArgsConstructor
public class NightmareNightScheduler implements EventScheduler {
    private static final int CHANCE_RANGE = 100;
    private static final long DELAY = 0L;
    private final JavaPlugin plugin;
    private final NightmareNightConfig config;
    private final Random random;
    private int taskId = -1;
    private List<World> worlds;
    private Set<World> checkedWorlds;
    private int eventChance;

    @Override
    public void start() {
        cache();
        taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(
                plugin, this::processWorlds, DELAY, config.getCheckFrequency()).getTaskId();
    }

    private void processWorlds() {
        for (final World world : worlds) {
            if (world == null) {
                LoggerUtility.warn(this, "Could not find world because it's null");
                continue;
            }

            // Check process
            if (!GameTimeUtility.isNight(world)) {
                checkedWorlds.remove(world);
                continue;
            } else if (checkedWorlds.contains(world)) {
                continue;
            } else if (!passesChance()) {
                continue;
            }

            // Handle the rest of stuff
            int a = 5;
        }
    }

    private boolean passesChance() {
        final int randomNumber = random.nextInt(CHANCE_RANGE);
        return randomNumber <= eventChance;
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

    private void cache() {
        worlds = config.getAllowedWorlds();
        eventChance = config.getEventChance();
    }
}
