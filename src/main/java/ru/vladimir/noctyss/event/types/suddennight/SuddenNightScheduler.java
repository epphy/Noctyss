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

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class SuddenNightScheduler implements EventScheduler {
    private static final EventType EVENT_TYPE = EventType.SUDDEN_NIGHT;
    private final JavaPlugin plugin;
    private final PluginManager pluginManager;
    private final ProtocolManager protocolManager;
    private final SuddenNightConfig config;
    private final MessageConfig messageConfig;
    private final Random random;
    private List<World> worlds;
    private long periodTicks;
    private double eventChance;
    private long cooldownDays;
    private int taskId = -1;

    {
        cache();
    }

    @Override
    public void start() {
        taskId = Bukkit.getScheduler().runTaskTimer(
                plugin, this::processWorlds, 0L, periodTicks).getTaskId();
    }

    private void processWorlds() {
        for (final World world : worlds) {
            if (isEligible(world)) startEvent(world);
        }
    }

    private boolean isEligible(World world) {
        return isAvailableForEvent(world) &&     // Whether it is available
               GameTimeUtility.isDay(world) &&   // Whether there is a day
               isChancePassed() &&               // Whether the chance is passing
               !isCooldown(world);               // Whether the world is in cooldown
    }

    private boolean isAvailableForEvent(World world) {
        return !EventAPI.isEventActive(world, EVENT_TYPE) &&  // Whether the event is already active
               !EventAPI.isAnyEventActive(world);             // Whether there's already an ongoing event
    }

    private boolean isChancePassed() {
        return random.nextDouble() <= eventChance;
    }

    private boolean isCooldown(World world) {
        final long currentDay = GameTimeUtility.getDay(world);
        final long lastTimeDayEvent = EventAPI.getLastDayTheEventWas(world, EVENT_TYPE);
        return (currentDay - lastTimeDayEvent) < cooldownDays;
    }

    @Override
    public void startEvent(World world) {
        final var eventInstance = new SuddenNightInstance(
                plugin,
                pluginManager,
                protocolManager,
                config,
                messageConfig,
                world);
        EventManager.startEvent(world, EVENT_TYPE, eventInstance);
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
        periodTicks = config.getCheckFrequencyTicks();
        eventChance = config.getEventChance();
        cooldownDays = config.getCooldownDays();
    }

    @Override
    public String toString() {
        return "SuddenNightScheduler{" +
                "taskId=" + taskId +
                ", cooldownDays=" + cooldownDays +
                ", eventChance=" + eventChance +
                ", checkFrequencyTicks=" + periodTicks +
                ", worlds=" + worlds.stream().map(World::getName).collect(Collectors.toSet()) +
                '}';
    }
}
