package ru.vladimir.noctyss.event.types.suddennight;

import com.comphenix.protocol.ProtocolManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.api.EventAPI;
import ru.vladimir.noctyss.config.ConfigService;
import ru.vladimir.noctyss.event.EventManager;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.event.types.EventScheduler;
import ru.vladimir.noctyss.utility.GameTimeUtility;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.Random;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public final class SuddenNightScheduler implements EventScheduler {
    private static final EventType EVENT_TYPE = EventType.SUDDEN_NIGHT;
    private static final long DELAY = 0L;
    private final JavaPlugin plugin;
    private final PluginManager pluginManager;
    private final ProtocolManager protocolManager;
    private final EventManager eventManager;
    private final Random random;
    private Set<UUID> worldIds;
    private long checkFrequencyTicks;
    private double eventChance;
    private long cooldownDays;
    private int taskId = -1;

    @Override
    public void start() {
        cache();
        taskId = Bukkit.getScheduler().runTaskTimer(
                plugin, this::processWorlds, DELAY, checkFrequencyTicks).getTaskId();
    }

    private void processWorlds() {
        for (final UUID worldId : worldIds) {

            final World world = Bukkit.getWorld(worldId);
            if (world == null) {
                LoggerUtility.warn(this, "Failed to process a world because it's null");
                continue;
            }

            if (isEligible(world)) startEvent(world);
        }
    }

    private boolean isEligible(World world) {
        return !EventAPI.isEventActive(world, EVENT_TYPE) && // Whether the event is already active
               GameTimeUtility.isDay(world) &&               // Whether there is a day
               isPassingChance() &&                          // Whether the chance is passing
               !isCooldown(world);                           // Whether the world is in cooldown
    }

    private boolean isPassingChance() {
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
                plugin, pluginManager, protocolManager, eventManager, world);
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
        worldIds = EventAPI.getWorldIdsWithAllowedEvent(EVENT_TYPE);
        checkFrequencyTicks = ConfigService.getSuddenNightConfig().getCheckFrequencyTicks();
        eventChance = ConfigService.getSuddenNightConfig().getEventChance();
        cooldownDays = ConfigService.getSuddenNightConfig().getCooldownDays();
    }

    @Override
    public String toString() {
        return "SuddenNightScheduler{" +
                "taskId=" + taskId +
                ", cooldownDays=" + cooldownDays +
                ", eventChance=" + eventChance +
                ", checkFrequencyTicks=" + checkFrequencyTicks +
                ", worlds=" + worldIds +
                '}';
    }
}
