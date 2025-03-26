package ru.vladimir.votvproduction;

import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.votvproduction.api.EventAPI;
import ru.vladimir.votvproduction.api.WorldStateConfigurer;
import ru.vladimir.votvproduction.config.*;
import ru.vladimir.votvproduction.event.*;
import ru.vladimir.votvproduction.event.modules.notification.storage.PlayerNotificationSerializer;
import ru.vladimir.votvproduction.event.modules.notification.storage.PlayerNotificationService;
import ru.vladimir.votvproduction.event.modules.notification.storage.PlayerNotificationStorage;
import ru.vladimir.votvproduction.utility.GameTimeUtility;
import ru.vladimir.votvproduction.utility.LoggerUtility;

import java.util.logging.Level;

public final class VOTVProduction extends JavaPlugin {
    private ConfigService configService;

    /*

    STARTUP LOGIC

     */

    @Override
    public void onEnable() {
        loadUtilities();
        loadConfig();
        configureLogger();
        loadAPI(); // TODO
        loadScheduler(); // TODO
        startupMessage();
    }

    private void loadUtilities() {
        LoggerUtility.init(getLogger());
        GameTimeUtility.init(this);
    }

    private void loadConfig() {
        configService = new ConfigService(this);
        configService.init();
    }

    private void configureLogger() {
        switch (configService.getGeneralConfig().getDebugLevel()) {
            case 1 -> LoggerUtility.setLevel(Level.INFO); // Debug
            case 2 -> LoggerUtility.setLevel(Level.ALL); // Extra detailed debug
            default -> LoggerUtility.setLevel(Level.WARNING); // Default
        }
    }

    private void loadAPI() { // TODO
        WorldStateConfigurer worldStateConfigurer = new WorldStateConfigurer(configService.getGeneralConfig()); // TODO
        EventAPI.init(worldStateConfigurer); // TODO
    }

    private void loadScheduler() { // TODO
        EventManager eventManager = new EventManager(); // TODO
        PlayerNotificationService service = new PlayerNotificationService( // TODO
                new PlayerNotificationStorage(this), // TODO
                new PlayerNotificationSerializer()); // TODO
        service.init(); // TODO
        GlobalEventScheduler eventScheduler = new GlobalEventScheduler(this, service, getServer().getPluginManager(), configService, eventManager); // TODO
        eventScheduler.start(); // TODO
    }

    private void startupMessage() {
        LoggerUtility.announce("Welcome! Thanks for choosing us!");
    }

    /*

    SHUTDOWN LOGIC

     */

    @Override
    public void onDisable() {
        shutdownMessage();
    }

    private void shutdownMessage() {
        LoggerUtility.announce("Have a nice day!");
    }
}
