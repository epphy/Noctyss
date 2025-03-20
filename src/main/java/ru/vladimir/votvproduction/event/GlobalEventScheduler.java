package ru.vladimir.votvproduction.event;

import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.votvproduction.config.ConfigService;
import ru.vladimir.votvproduction.event.types.EventScheduler;
import ru.vladimir.votvproduction.event.types.nightmarenight.NightmareNightScheduler;
import ru.vladimir.votvproduction.utility.LoggerUtility;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RequiredArgsConstructor
public class GlobalEventScheduler implements EventScheduler {
    private final JavaPlugin plugin;
    private final ConfigService configService;
    private final Map<EventType, EventScheduler> eventSchedulers = new HashMap<>();

    @Override
    public void start() {
        startNightmareNightScheduler();
    }

    private void startNightmareNightScheduler() {
        final NightmareNightScheduler scheduler = new NightmareNightScheduler(
                plugin, configService.nightmareNightConfig(), new Random());
        scheduler.start();
        eventSchedulers.put(EventType.NIGHTMARENIGHT, scheduler);
        LoggerUtility.info(this, "Nightmare Night scheduler started");
    }

    @Override
    public void stop() {
        stopNightmareNightScheduler();
    }

    private void stopNightmareNightScheduler() {
        if (!eventSchedulers.containsKey(EventType.NIGHTMARENIGHT)) {
            LoggerUtility.warn(this, "Nightmare Night scheduler is not active to be stopped");
            return;
        }
        eventSchedulers.get(EventType.NIGHTMARENIGHT).stop();
        eventSchedulers.remove(EventType.NIGHTMARENIGHT);
        LoggerUtility.info(this, "Nightmare Night scheduler stopped");
    }
}
