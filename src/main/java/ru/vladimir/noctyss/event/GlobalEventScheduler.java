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
public class GlobalEventScheduler implements Controllable {
    private final EnumMap<EventType, EventScheduler> eventSchedulers = new EnumMap<>(EventType.class);
    private final JavaPlugin plugin;
    private final PluginManager pluginManager;
    private final ProtocolManager protocolManager;
    private final EventManager eventManager;

    @Override
    public void start() {
        addNightmareNight();
        addSuddenNight();
        LoggerUtility.info(this, "All events have been enabled");
    }

    private void addNightmareNight() {
        if (ConfigService.getInstance().getNightmareNightConfig().isEventEnabled()) {
            final var scheduler = new NightmareNightScheduler(
                    plugin,
                    protocolManager,
                    pluginManager,
                    eventManager,
                    ConfigService.getInstance().getNightmareNightConfig(),
                    ConfigService.getInstance().getMessageConfig(),
                    new Random());
            scheduler.start();
            eventSchedulers.put(EventType.NIGHTMARE_NIGHT, scheduler);
            LoggerUtility.info(this, "NightmareNight event has been enabled");
        } else {
            LoggerUtility.info(this, "NightmareNight event has been disabled");
        }
    }

    private void addSuddenNight() {
        if (ConfigService.getInstance().getSuddenNightConfig().isEventEnabled()) {
            final var scheduler = new SuddenNightScheduler(
                    plugin,
                    pluginManager,
                    protocolManager,
                    eventManager,
                    ConfigService.getInstance().getSuddenNightConfig(),
                    ConfigService.getInstance().getMessageConfig(),
                    new Random());
            scheduler.start();
            eventSchedulers.put(EventType.SUDDEN_NIGHT, scheduler);
            LoggerUtility.info(this, "SuddenNight event has been enabled");
        } else {
            LoggerUtility.info(this, "SuddenNight event has been disabled");
        }
    }

    @Override
    public void stop() {
        removeNightmareNight();
        removeSuddenNight();
        LoggerUtility.info(this, "All events have been disabled");
    }

    private void removeNightmareNight() {
        if (ConfigService.getInstance().getNightmareNightConfig().isEventEnabled() &&
            eventSchedulers.containsKey(EventType.NIGHTMARE_NIGHT)) {

            eventSchedulers.get(EventType.NIGHTMARE_NIGHT).stop();
            eventSchedulers.remove(EventType.NIGHTMARE_NIGHT);
        }
    }

    private void removeSuddenNight() {
        if (ConfigService.getInstance().getSuddenNightConfig().isEventEnabled() &&
            eventSchedulers.containsKey(EventType.SUDDEN_NIGHT)) {

            eventSchedulers.get(EventType.SUDDEN_NIGHT).stop();
            eventSchedulers.remove(EventType.SUDDEN_NIGHT);
        }
    }
}
