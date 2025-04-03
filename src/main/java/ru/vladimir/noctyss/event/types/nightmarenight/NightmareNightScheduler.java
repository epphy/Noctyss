package ru.vladimir.noctyss.event.types.nightmarenight;

import com.comphenix.protocol.ProtocolManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.api.EventAPI;
import ru.vladimir.noctyss.config.MessageConfig;
import ru.vladimir.noctyss.config.NightmareNightConfig;
import ru.vladimir.noctyss.event.EventManager;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.event.types.EventScheduler;
import ru.vladimir.noctyss.utility.GameTimeUtility;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@RequiredArgsConstructor
public final class NightmareNightScheduler implements EventScheduler {
    private static final EventType EVENT_TYPE = EventType.NIGHTMARE_NIGHT;
    private static final int CHANCE_RANGE = 100;
    private static final long DELAY = 0L;
    private final JavaPlugin plugin;
    private final ProtocolManager protocolManager;
    private final PluginManager pluginManager;
    private final EventManager eventManager;
    private final NightmareNightConfig config;
    private final MessageConfig messageConfig;
    private final Random random;
    private final Set<World> checkedWorlds = new HashSet<>();
    private List<World> worlds;
    private long checkFrequency;
    private int eventChance;
    private int taskId = -1;

    @Override
    public void start() {
        cache();
        taskId = Bukkit.getScheduler().runTaskTimer(
                plugin, this::processWorlds, DELAY, checkFrequency).getTaskId();
    }

    private void processWorlds() {
        for (final World world : worlds) {

            if (!GameTimeUtility.isNight(world)) {
                checkedWorlds.remove(world);
                continue;
            }
            if (checkedWorlds.contains(world)) continue;
            if (!isAllowed(world) || !passesChance()) continue;

            checkedWorlds.add(world);
            startEvent(world);
        }
    }

    private boolean passesChance() {
        final int randomNumber = random.nextInt(CHANCE_RANGE);
        return randomNumber <= eventChance;
    }

    private boolean isAllowed(World world) {
        return !EventAPI.isEventActive(world, EVENT_TYPE) &&  // Whether the event is already active
               !EventAPI.isAnyEventActive(world);        // Whether there's already an ongoing event
    }

    @Override
    public void startEvent(World world) {
        final var eventInstance = new NightmareNightInstance(
                plugin, protocolManager, eventManager, pluginManager, config, messageConfig, world);
        eventManager.startEvent(world, EVENT_TYPE, eventInstance);
        LoggerUtility.info(this, "Scheduling event in: %s".formatted(world.getName()));
    }

    @Override
    public void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    private void cache() {
        worlds = EventAPI.getWorldsAllowingEvent(EVENT_TYPE);
        checkFrequency = config.getCheckFrequency();
        eventChance = config.getEventChance();
    }
}
