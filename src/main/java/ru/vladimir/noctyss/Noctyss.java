package ru.vladimir.noctyss;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.api.EventAPI;
import ru.vladimir.noctyss.command.NoctyssCommand;
import ru.vladimir.noctyss.config.ConfigService;
import ru.vladimir.noctyss.event.EventManager;
import ru.vladimir.noctyss.event.GlobalEventScheduler;
import ru.vladimir.noctyss.event.modules.notification.senders.NotificationManager;
import ru.vladimir.noctyss.event.modules.notification.storage.PlayerNotificationService;
import ru.vladimir.noctyss.utility.GameTimeUtility;
import ru.vladimir.noctyss.utility.LoggerUtility;
import ru.vladimir.noctyss.utility.TaskUtil;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;

public final class Noctyss extends JavaPlugin {
    private static final int CURRENT_VERSION = 101;
    private EventManager eventManager;
    private GlobalEventScheduler globalEventScheduler;

    /*

    STARTUP LOGIC

     */

    @Override
    public void onEnable() {
        checkUpdateVersion();
        loadUtilities();
        configureLogger();
        loadScheduler();
        loadCommand();
        startupMessage();
    }

    private void loadUtilities() {
        LoggerUtility.init(getLogger());
        TaskUtil.init(this);
        GameTimeUtility.init(this);
        PlayerNotificationService.init(this);
        NotificationManager.init();
        ConfigService.init(this);
        ConfigService.loadGeneralConfig();
        EventAPI.init();
        ConfigService.loadOtherConfigs();
    }

    private void configureLogger() {
        switch (ConfigService.getGeneralConfig().getDebugLevel()) {
            case 1 -> LoggerUtility.setLevel(Level.INFO);       // Debug
            case 2 -> LoggerUtility.setLevel(Level.ALL);        // Extra detailed debug
            default -> LoggerUtility.setLevel(Level.WARNING);   // Standard
        }
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

    private void checkUpdateVersion() {
        TaskUtil.runTaskAsync(this, () -> {
            LoggerUtility.info(this, "Checking the latest version...");

            final String latestVersion = getLatestVersion();
            if (latestVersion == null) {
                LoggerUtility.err(this, "Failed to check update version");
                return;
            }

            if (isNewerVersion(latestVersion)) {
                LoggerUtility.info(this, "A new update is available! Latest version: " + latestVersion);
                LoggerUtility.info(this, "Download it here: https://github.com/epphy/Noctyss/releases/latest");
            } else {
                LoggerUtility.info(this, "You're running the latest version!");
            }
        });
    }

    private String getLatestVersion() {
        try (final HttpClient httpClient = HttpClient.newHttpClient();) {
            final HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.github.com/repos/epphy/Noctyss/releases/latest"))
                    .header("Accept", "application/vnd.github.v3+json")
                    .build();

            final HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return null;

            final JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            return json.get("tag_name").getAsString();
        } catch (IOException | InterruptedException e) {
            LoggerUtility.err(this, "Error fetching latest version: %s".formatted(e.getMessage()));
            return null;
        }
    }

    private boolean isNewerVersion(String latestVersion) {
        latestVersion = latestVersion.startsWith("v") ? latestVersion.substring(1) : latestVersion;
        final int latestVersionNumber = Integer.parseInt(latestVersion.replace(".", ""));
        return CURRENT_VERSION < latestVersionNumber;
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
