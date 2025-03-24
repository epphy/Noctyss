package ru.vladimir.votvproduction.event.types.nightmarenight;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.votvproduction.api.EventAPI;
import ru.vladimir.votvproduction.config.MessageConfig;
import ru.vladimir.votvproduction.config.NightmareNightConfig;
import ru.vladimir.votvproduction.event.EventManager;
import ru.vladimir.votvproduction.event.EventType;
import ru.vladimir.votvproduction.event.types.EventScheduler;
import ru.vladimir.votvproduction.utility.GameTimeUtility;
import ru.vladimir.votvproduction.utility.LoggerUtility;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@RequiredArgsConstructor
public final class NightmareNightScheduler implements EventScheduler {
    private static final int CHANCE_RANGE = 100;
    private static final long DELAY = 0L;
    private final JavaPlugin plugin;
    private final PluginManager pluginManager;
    private final EventManager eventManager;
    private final NightmareNightConfig config;
    private final MessageConfig messageConfig;
    private final Random random;
    private final Set<World> checkedWorlds = new HashSet<>();
    private List<World> worlds;
    private int eventChance;
    private int taskId = -1;

    @Override
    public void start() {
        if (config.isEventEnabled()) {
            cache();
            taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(
                    plugin, this::processWorlds, DELAY, config.getCheckFrequency()).getTaskId();
            LoggerUtility.info(this, "Started scheduler");
        }
    }

    private void processWorlds() {
        for (final World world : worlds) {
            if (world == null) {
                LoggerUtility.warn(this, "Could not find world because it's null");
                continue;
            }

            if (!GameTimeUtility.isNight(world)) {
                checkedWorlds.remove(world);
                continue;
            }

            if (checkedWorlds.contains(world)) {
                continue;
            }

            if (!passesChance() || EventAPI.isEventActive(world, EventType.NIGHTMARE_NIGHT))
                continue;

            checkedWorlds.add(world);
            final NightmareNightInstance eventInstance = new NightmareNightInstance(
                    plugin, eventManager, pluginManager, config, messageConfig, world);
            eventManager.startEvent(world, EventType.NIGHTMARE_NIGHT, eventInstance);
            LoggerUtility.info(this, "Scheduling event for world %s".formatted(world));
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
        worlds = EventAPI.getWorldsWithAllowedEvent(EventType.NIGHTMARE_NIGHT);
        eventChance = config.getEventChance();
        LoggerUtility.info(this, "Cached fields");
    }
}
