package ru.vladimir.noctyss;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.api.EventAPI;
import ru.vladimir.noctyss.api.WorldStateConfigurer;
import ru.vladimir.noctyss.config.*;
import ru.vladimir.noctyss.event.*;
import ru.vladimir.noctyss.event.modules.notification.storage.PlayerNotificationSerializer;
import ru.vladimir.noctyss.event.modules.notification.storage.PlayerNotificationService;
import ru.vladimir.noctyss.event.modules.notification.storage.PlayerNotificationStorage;
import ru.vladimir.noctyss.utility.GameTimeUtility;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.logging.Level;

public final class Noctyss extends JavaPlugin {
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
        ConfigService.init(this);
    }

    private void configureLogger() {
        switch (ConfigService.getGeneralConfig().getDebugLevel()) {
            case 1 -> LoggerUtility.setLevel(Level.INFO); // Debug
            case 2 -> LoggerUtility.setLevel(Level.ALL); // Extra detailed debug
            default -> LoggerUtility.setLevel(Level.WARNING); // Default
        }
    }

    private void loadAPI() { // TODO
        WorldStateConfigurer worldStateConfigurer = new WorldStateConfigurer(ConfigService.getGeneralConfig()); // TODO
        EventAPI.init(worldStateConfigurer); // TODO
    }

    private void loadScheduler() { // TODO
        EventManager eventManager = new EventManager(); // TODO
        PlayerNotificationService service = new PlayerNotificationService( // TODO
                new PlayerNotificationStorage(this), // TODO
                new PlayerNotificationSerializer()); // TODO
        service.init(); // TODO
        PluginManager pluginManager = getServer().getPluginManager(); // TODO
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager(); // TODO
        GlobalEventScheduler eventScheduler = new GlobalEventScheduler( // TODO
                this, service, pluginManager, protocolManager, configService, eventManager); // TODO
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
