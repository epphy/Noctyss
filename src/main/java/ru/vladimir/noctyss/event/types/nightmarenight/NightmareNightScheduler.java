package ru.vladimir.noctyss.event.types.nightmarenight;

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

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public final class NightmareNightScheduler implements EventScheduler {
    private static final EventType EVENT_TYPE = EventType.NIGHTMARE_NIGHT;
    private static final int CHANCE_RANGE = 100;
    private static final long DELAY = 0L;
    private final JavaPlugin plugin;
    private final ProtocolManager protocolManager;
    private final PluginManager pluginManager;
    private final EventManager eventManager;
    private final Random random;
    private final Set<UUID> checkedWorldsIds = new HashSet<>();
    private Set<UUID> worldsIds;
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
        for (final UUID worldUid : worldsIds) {

            final World world = Bukkit.getWorld(worldUid);
            if (world == null) {
                LoggerUtility.warn(this, "Failed to process a world because it's null");
                continue;
            }

            if (!GameTimeUtility.isNight(world)) {
                checkedWorldsIds.remove(worldUid);
                continue;
            }
            if (checkedWorldsIds.contains(worldUid)) continue;
            if (!passesChance() || EventAPI.isEventActive(world, EVENT_TYPE)) continue;

            checkedWorldsIds.add(worldUid);
            startEvent(world);
        }
    }

    private boolean passesChance() {
        final int randomNumber = random.nextInt(CHANCE_RANGE);
        return randomNumber <= eventChance;
    }

    @Override
    public void startEvent(World world) {
        final var eventInstance = new NightmareNightInstance(
                plugin, protocolManager, eventManager, pluginManager, world);
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
        worldsIds = EventAPI.getWorldIdsWithAllowedEvent(EVENT_TYPE);
        checkFrequency = ConfigService.getNightmareNightConfig().getCheckFrequency();
        eventChance = ConfigService.getNightmareNightConfig().getEventChance();
    }
}
