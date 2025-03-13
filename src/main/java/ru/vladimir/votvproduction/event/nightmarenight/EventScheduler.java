package ru.vladimir.votvproduction.event.nightmarenight;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.votvproduction.config.NightmareNightConfig;
import ru.vladimir.votvproduction.event.AbstractEventScheduler;
import ru.vladimir.votvproduction.utility.LoggerUtility;
import ru.vladimir.votvproduction.utility.RandomNumberUtility;
import ru.vladimir.votvproduction.utility.TimeUtility;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class EventScheduler implements AbstractEventScheduler {
    private static final long SCHEDULER_START_DELAY_TICKS = 0L;
    private static final long SCHEDULER_FREQUENCY_TICKS = 100L;
    private final JavaPlugin plugin;
    private final NightmareNightConfig config;
    private final List<World> checkedWorlds = new ArrayList<>();
    private List<World> worlds;
    private int chance;

    @Override
    public void start() {
        cache();
        Bukkit.getScheduler().runTaskTimerAsynchronously(
                plugin, this::processWorlds, SCHEDULER_START_DELAY_TICKS, SCHEDULER_FREQUENCY_TICKS);
        LoggerUtility.info(this.getClass(), "EventScheduler started");
    }

    private void processWorlds() {
        for (final World world : worlds) {
            checkEligibility(world);
        }
    }

    private void checkEligibility(World world) {
        if (TimeUtility.isNight(world) && !checkedWorlds.contains(world)) {
            if (shouldStart()) startEvent(world);
            checkedWorlds.add(world);
            return;
        }
        checkedWorlds.remove(world);
    }

    private boolean shouldStart() {
        return RandomNumberUtility.isWithinChance(chance);
    }

    private void startEvent(World world) {
        // Start event logic
    }

    private void cache() {
        worlds = config.getAllowedWorlds();
        chance = config.getEventChance();
    }
}
