package ru.vladimir.votvproduction.event;

import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.votvproduction.config.ConfigService;
import ru.vladimir.votvproduction.event.modules.notification.storage.PlayerNotificationService;
import ru.vladimir.votvproduction.event.types.EventScheduler;
import ru.vladimir.votvproduction.event.types.nightmarenight.NightmareNightScheduler;
import ru.vladimir.votvproduction.utility.LoggerUtility;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RequiredArgsConstructor
public final class GlobalEventScheduler implements EventScheduler {
    private static final Map<EventType, EventScheduler> EVENT_SCHEDULERS = new HashMap<>();
    private final JavaPlugin plugin;
    private final PlayerNotificationService service;
    private final PluginManager pluginManager;
    private final ConfigService configService;
    private final EventManager eventManager;

    @Override
    public void start() {
        addNightmareNight();
    }

    private void addNightmareNight() {
        final NightmareNightScheduler scheduler = new NightmareNightScheduler(
                plugin, service, pluginManager, eventManager, configService.getNightmareNightConfig(),
                configService.getMessageConfig(), new Random());
        EVENT_SCHEDULERS.put(EventType.NIGHTMARE_NIGHT, scheduler);
        scheduler.start();
        LoggerUtility.info(this, "NightmareNight Scheduler has been booted");
    }

    @Override
    public void stop() {
        // TODO: Not required yet
    }
}
