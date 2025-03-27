package ru.vladimir.noctyss.event;

import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.config.ConfigService;
import ru.vladimir.noctyss.event.modules.notification.storage.PlayerNotificationService;
import ru.vladimir.noctyss.event.types.EventScheduler;
import ru.vladimir.noctyss.event.types.nightmarenight.NightmareNightScheduler;
import ru.vladimir.noctyss.event.types.suddennight.SuddenNightScheduler;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RequiredArgsConstructor
public class GlobalEventScheduler implements EventScheduler {
    private final Map<EventType, EventScheduler> EVENT_SCHEDULERS = new HashMap<>();
    private final JavaPlugin plugin;
    private final PlayerNotificationService service;
    private final PluginManager pluginManager;
    private final ConfigService configService;
    private final EventManager eventManager;

    @Override
    public void start() {
        addNightmareNight();
        addSuddenNight();
    }

    private void addNightmareNight() {
        final NightmareNightScheduler scheduler = new NightmareNightScheduler(
                plugin, service, pluginManager, eventManager, configService.getNightmareNightConfig(),
                configService.getMessageConfig(), new Random());
        EVENT_SCHEDULERS.put(EventType.NIGHTMARE_NIGHT, scheduler);
        scheduler.start();
        LoggerUtility.info(this, "NightmareNightScheduler has been started");
    }

    private void addSuddenNight() {
        final SuddenNightScheduler scheduler = new SuddenNightScheduler(plugin, configService.getSuddenNightConfig(),
                eventManager, new Random());
        EVENT_SCHEDULERS.put(EventType.SUDDEN_NIGHT, scheduler);
        scheduler.start();
        LoggerUtility.info(this, "SuddenNightScheduler has been started");
    }

    @Override
    public void stop() {
        removeNightmareNight();
        removeSuddenNight();
    }

    private void removeNightmareNight() {
        EVENT_SCHEDULERS.get(EventType.NIGHTMARE_NIGHT).stop();
        EVENT_SCHEDULERS.remove(EventType.NIGHTMARE_NIGHT);
        LoggerUtility.info(this, "NightmareNightScheduler has been stopped");
    }

    private void removeSuddenNight() {
        EVENT_SCHEDULERS.get(EventType.SUDDEN_NIGHT).stop();
        EVENT_SCHEDULERS.remove(EventType.SUDDEN_NIGHT);
        LoggerUtility.info(this, "SuddenNightScheduler has been stopped");
    }
}
