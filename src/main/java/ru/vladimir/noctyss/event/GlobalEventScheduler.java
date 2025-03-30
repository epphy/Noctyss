package ru.vladimir.noctyss.event;

import com.comphenix.protocol.ProtocolManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.config.ConfigService;
import ru.vladimir.noctyss.event.modules.notification.storage.PlayerNotificationService;
import ru.vladimir.noctyss.event.types.EventScheduler;
import ru.vladimir.noctyss.event.types.nightmarenight.NightmareNightScheduler;
import ru.vladimir.noctyss.event.types.suddennight.SuddenNightScheduler;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.EnumMap;
import java.util.Random;

@RequiredArgsConstructor
public class GlobalEventScheduler implements EventScheduler {
    private final EnumMap<EventType, EventScheduler> eventSchedulers = new EnumMap<>(EventType.class);
    private final JavaPlugin plugin;
    private final PlayerNotificationService service;
    private final PluginManager pluginManager;
    private final ProtocolManager protocolManager;
    private final ConfigService configService;
    private final EventManager eventManager;

    @Override
    public void start() {
        addNightmareNight();
        addSuddenNight();
    }

    private void addNightmareNight() {
        if (ConfigService.getNightmareNightConfig().isEventEnabled()) {
            final NightmareNightScheduler scheduler = new NightmareNightScheduler(
                    plugin, protocolManager, service, pluginManager, eventManager, ConfigService.getNightmareNightConfig(),
                    ConfigService.getMessageConfig(), new Random());
            eventSchedulers.put(EventType.NIGHTMARE_NIGHT, scheduler);
            scheduler.start();
            LoggerUtility.info(this, "NightmareNight event has been enabled");
        } else {
            LoggerUtility.info(this, "NightmareNight event has been disabled");
        }
    }

    private void addSuddenNight() {
        if (ConfigService.getSuddenNightConfig().isEventEnabled()) {
            final SuddenNightScheduler scheduler = new SuddenNightScheduler(
                    plugin, pluginManager, protocolManager, ConfigService.getSuddenNightConfig(),
                    ConfigService.getMessageConfig(), eventManager, new Random());
            eventSchedulers.put(EventType.SUDDEN_NIGHT, scheduler);
            scheduler.start();
            LoggerUtility.info(this, "SuddenNight event has been enabled");
        } else {
            LoggerUtility.info(this, "SuddenNight event has been disabled");
        }
    }

    @Override
    public void stop() {
        removeNightmareNight();
        removeSuddenNight();
    }

    private void removeNightmareNight() {
        if (ConfigService.getNightmareNightConfig().isEventEnabled()) {
            eventSchedulers.get(EventType.NIGHTMARE_NIGHT).stop();
            eventSchedulers.remove(EventType.NIGHTMARE_NIGHT);
            LoggerUtility.info(this, "NightmareNight event has been stopped");
        }
    }

    private void removeSuddenNight() {
        if (ConfigService.getSuddenNightConfig().isEventEnabled()) {
            eventSchedulers.get(EventType.SUDDEN_NIGHT).stop();
            eventSchedulers.remove(EventType.SUDDEN_NIGHT);
            LoggerUtility.info(this, "SuddenNight event has been stopped");
        }
    }
}
