package ru.vladimir.noctyss;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.api.EventAPI;
import ru.vladimir.noctyss.api.WorldStateManagerProvider;
import ru.vladimir.noctyss.command.NoctyssCommand;
import ru.vladimir.noctyss.config.ConfigService;
import ru.vladimir.noctyss.event.EventManager;
import ru.vladimir.noctyss.event.GlobalEventScheduler;
import ru.vladimir.noctyss.event.modules.notification.senders.NotificationManager;
import ru.vladimir.noctyss.event.modules.notification.storage.PlayerNotificationService;
import ru.vladimir.noctyss.utility.GameTimeUtility;
import ru.vladimir.noctyss.utility.LoggerUtility;
import ru.vladimir.noctyss.utility.TaskUtil;

import java.util.logging.Level;

public final class Noctyss extends JavaPlugin {
    private EventManager eventManager;
    private GlobalEventScheduler globalEventScheduler;

    /*

    STARTUP LOGIC

     */

    @Override
    public void onEnable() {
        loadUtilities();
        configureLogger();
        loadAPI();
        loadScheduler();
        loadCommand();
        startupMessage();
    }

    private void loadUtilities() {
        LoggerUtility.init(getLogger());
        TaskUtil.init(this);
        GameTimeUtility.init(this);
        ConfigService.init(this);
        PlayerNotificationService.init(this);
        NotificationManager.init();
    }

    private void configureLogger() {
        switch (ConfigService.getGeneralConfig().getDebugLevel()) {
            case 1 -> LoggerUtility.setLevel(Level.INFO);       // Debug
            case 2 -> LoggerUtility.setLevel(Level.ALL);        // Extra detailed debug
            default -> LoggerUtility.setLevel(Level.WARNING);   // Standard
        }
    }

    private void loadAPI() {
        WorldStateManagerProvider worldStateManagerProvider = new WorldStateManagerProvider();
        EventAPI.init(worldStateManagerProvider);
    }

    private void loadScheduler() {
        eventManager = new EventManager();
        final PluginManager pluginManager = getServer().getPluginManager();
        final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        globalEventScheduler = new GlobalEventScheduler(
                this, pluginManager, protocolManager, eventManager);
        globalEventScheduler.start();
    }

    private void loadCommand() {
        final PluginCommand command = getServer().getPluginCommand("noctyss");
        if (command == null) {
            LoggerUtility.err(this, "Failed to load the main command");
            return;
        }

        final NoctyssCommand commandHandler = new NoctyssCommand(
                this, getServer().getPluginManager(), eventManager, globalEventScheduler);
        commandHandler.init();
        command.setExecutor(commandHandler);
        command.setTabCompleter(commandHandler);
    }

    private void startupMessage() {
        LoggerUtility.announce("Welcome! Thanks for choosing us!");
    }

    /*

    SHUTDOWN LOGIC

     */

    @Override
    public void onDisable() {
        TaskUtil.setShuttingDown(true);
        stopScheduler();
        stopAllEvents();
        unloadNecessaryUtilities();
        shutdownMessage();
    }

    private void stopScheduler() {
        if (globalEventScheduler != null) {
            globalEventScheduler.stop();
        }
    }

    private void stopAllEvents() {
        eventManager.stopAllEvents();
    }

    private void unloadNecessaryUtilities() {
        PlayerNotificationService.updateStorage();
    }

    private void shutdownMessage() {
        LoggerUtility.announce("Have a nice day! :)");
    }
}
