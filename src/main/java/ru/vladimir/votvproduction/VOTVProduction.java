package ru.vladimir.votvproduction;

import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.votvproduction.api.EventAPI;
import ru.vladimir.votvproduction.api.WorldStateConfigurer;
import ru.vladimir.votvproduction.config.*;
import ru.vladimir.votvproduction.event.*;
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
        initLogger();
        loadConfig();
        configureLogger();
        loadGameTimeUtility(); // TODO
        loadAPI(); // TODO
        loadScheduler(); // TODO
        startupMessage();
    }

    private void initLogger() {
        LoggerUtility.init(getLogger());
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

    private void loadGameTimeUtility() { // TODO
        GameTimeUtility.init(this); // TODO
    }

    private void loadAPI() { // TODO
        WorldStateConfigurer worldStateConfigurer = new WorldStateConfigurer(configService.getGeneralConfig()); // TODO
        EventAPI.init(worldStateConfigurer); // TODO
    }

    private void loadScheduler() { // TODO
        EventManager eventManager = new EventManager(); // TODO
        GlobalEventScheduler eventScheduler = new GlobalEventScheduler(this, getServer().getPluginManager(), configService, eventManager); // TODO
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
