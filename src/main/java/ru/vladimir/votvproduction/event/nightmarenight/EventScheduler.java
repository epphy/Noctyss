package ru.vladimir.votvproduction.event.nightmarenight;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.votvproduction.config.NightmareNightConfig;
import ru.vladimir.votvproduction.event.AbstractEventScheduler;
import ru.vladimir.votvproduction.manager.customevent.NightStartEvent;
import ru.vladimir.votvproduction.utility.LoggerUtility;
import ru.vladimir.votvproduction.utility.TimeUtility;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class EventScheduler implements AbstractEventScheduler {
    private static final long SCHEDULER_START_DELAY_TICKS = 0L;
    private static final long SCHEDULER_FREQUENCY_TICKS = 100L;
    private final List<World> checkedWorlds = new ArrayList<>();
    private final JavaPlugin plugin;
    private final PluginManager pluginManager;
    private final NightmareNightConfig config;
    private List<World> worlds;

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
            callEvent(world);
            checkedWorlds.add(world);
            return;
        }
        checkedWorlds.remove(world);
    }

    private void callEvent(World world) {
        pluginManager.callEvent(new NightStartEvent(world));
    }

    private void cache() {
        worlds = config.getAllowedWorlds();
    }
}
