package ru.vladimir.noctyss.event;

import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.config.ConfigService;
import ru.vladimir.noctyss.event.types.EventScheduler;
import ru.vladimir.noctyss.event.types.nightmarenight.NightmareNightScheduler;
import ru.vladimir.noctyss.event.types.suddennight.SuddenNightScheduler;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.EnumMap;
import java.util.Random;

@Getter
@RequiredArgsConstructor
public final class GlobalEventScheduler implements Controllable {
    private final EnumMap<EventType, EventScheduler> eventSchedulers = new EnumMap<>(EventType.class);
    private final JavaPlugin plugin;
    private final PluginManager pluginManager;
    private final ProtocolManager protocolManager;

    @Override
    public void start() {
        addNightmareNight();
        addSuddenNight();
        LoggerUtility.info(this, "All events have been enabled");
    }

    private void addNightmareNight() {
        addScheduler(EventType.NIGHTMARE_NIGHT, new NightmareNightScheduler(
                plugin,
                protocolManager,
                pluginManager,
                ConfigService.getInstance().getNightmareNightConfig(),
                ConfigService.getInstance().getMessageConfig(),
                new Random()
        ), ConfigService.getInstance().getNightmareNightConfig().isEventEnabled());
    }

    private void addSuddenNight() {
        addScheduler(EventType.SUDDEN_NIGHT, new SuddenNightScheduler(
                plugin,
                pluginManager,
                protocolManager,
                ConfigService.getInstance().getSuddenNightConfig(),
                ConfigService.getInstance().getMessageConfig(),
                new Random()
        ), ConfigService.getInstance().getSuddenNightConfig().isEventEnabled());
    }

    private void addScheduler(EventType eventType, EventScheduler scheduler, boolean enabled) {
        if (!enabled) {
            LoggerUtility.info(this, "%s event has been disabled".formatted(eventType.name()));
            return;
        }

        scheduler.start();
        eventSchedulers.put(eventType, scheduler);
        LoggerUtility.info(this, "%s event has been enabled".formatted(eventType.name()));
    }

    @Override
    public void stop() {
        removeNightmareNight();
        removeSuddenNight();
        eventSchedulers.clear();
        LoggerUtility.info(this, "All events have been disabled");
    }

    private void removeNightmareNight() {
        removeScheduler(
                EventType.NIGHTMARE_NIGHT,
                ConfigService.getInstance().getNightmareNightConfig().isEventEnabled()
        );
    }

    private void removeSuddenNight() {
        removeScheduler(
                EventType.SUDDEN_NIGHT,
                ConfigService.getInstance().getSuddenNightConfig().isEventEnabled()
        );
    }

    private void removeScheduler(EventType eventType, boolean enabled) {
        if (!enabled || !eventSchedulers.containsKey(eventType)) return;

        eventSchedulers.get(eventType).stop();
        eventSchedulers.remove(eventType);
    }
}
