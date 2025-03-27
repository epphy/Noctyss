package ru.vladimir.noctyss.event.types.suddennight;

import com.comphenix.protocol.ProtocolManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.api.EventAPI;
import ru.vladimir.noctyss.config.MessageConfig;
import ru.vladimir.noctyss.config.SuddenNightConfig;
import ru.vladimir.noctyss.event.EventManager;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.event.types.EventScheduler;
import ru.vladimir.noctyss.utility.GameTimeUtility;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.Random;
import java.util.Set;

@RequiredArgsConstructor
public final class SuddenNightScheduler implements EventScheduler {
    private static final EventType eventType = EventType.SUDDEN_NIGHT;
    private static final long DELAY = 0L;
    private final JavaPlugin plugin;
    private final PluginManager pluginManager;
    private final ProtocolManager protocolManager;
    private final SuddenNightConfig config;
    private final MessageConfig messageConfig;
    private final EventManager eventManager;
    private final Random random;
    private Set<World> worlds;
    private long checkFrequencyTicks;
    private double eventChance;
    private long cooldownDays;
    private int taskId = -1;

    @Override
    public void start() {
        if (config.isEventEnabled()) {
            cache();
            taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(
                    plugin, this::processWorlds, DELAY, checkFrequencyTicks).getTaskId();
            LoggerUtility.info(this, "Started");
        } else {
            LoggerUtility.info(this, "Event is disabled");
        }
    }

    private void processWorlds() {
        for (final World world : worlds) {

            if (EventAPI.isEventActive(world, eventType)) continue;
            if (!GameTimeUtility.isDay(world)) continue;
            if (!isPassingChance()) continue;
            if (isCooldown(world)) continue;

            final SuddenNightInstance instance = new SuddenNightInstance(
                    plugin, pluginManager, protocolManager, eventManager, config, messageConfig, world);
            eventManager.startEvent(world, eventType, instance);
            LoggerUtility.info(this, "Scheduling event in: %s".formatted(world.getName()));
        }
    }

    private boolean isPassingChance() {
        return random.nextDouble() <= eventChance;
    }

    private boolean isCooldown(World world) {
        final long currentDay = GameTimeUtility.getDay(world);
        final long lastTimeDayEvent = EventAPI.getLastDayTheEventWas(world, eventType);
        return (currentDay - lastTimeDayEvent) < cooldownDays;
    }

    @Override
    public void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            LoggerUtility.info(this, "Stopped");
        }
    }

    private void cache() {
        worlds = EventAPI.getWorldsWithAllowedEvent(eventType);
        checkFrequencyTicks = config.getCheckFrequencyTicks();
        eventChance = config.getEventChance();
        cooldownDays = config.getCooldownDays();
    }

    @Override
    public String toString() {
        return "SuddenNightScheduler{" +
                "taskId=" + taskId +
                ", cooldownDays=" + cooldownDays +
                ", eventChance=" + eventChance +
                ", checkFrequencyTicks=" + checkFrequencyTicks +
                ", worlds=" + worlds +
                '}';
    }
}
