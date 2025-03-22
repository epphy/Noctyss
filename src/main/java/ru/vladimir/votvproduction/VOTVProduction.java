package ru.vladimir.votvproduction;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.votvproduction.api.EventAPI;
import ru.vladimir.votvproduction.config.*;
import ru.vladimir.votvproduction.event.*;
import ru.vladimir.votvproduction.utility.GameTimeUtility;
import ru.vladimir.votvproduction.utility.LoggerUtility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public final class VOTVProduction extends JavaPlugin {
    private ConfigService configService;

    /*

    STARTUP LOGIC

     */

    @Override
    public void onEnable() {
        initLogger();
        loadConfig(); // TODO
        loadGameTimeUtility(); // TODO
        loadAPI(); // TODO
        loadScheduler(); // TODO
        startupMessage();
    }

    private void initLogger() {
        LoggerUtility.init(getLogger());
        LoggerUtility.setLevel(Level.ALL); // TODO
    }

    private void loadConfig() { // TODO
        saveDefaultConfig(); // TODO
        NightmareNightConfig config = new NightmareNightConfig(this); // TODO
        MessageConfig messageConfig = new MessageConfig(this); // TODO
        config.load(); // TODO
        messageConfig.load(); // TODO
        configService = new ConfigService(new GeneralConfig(), config, new SuddenNightConfig(), messageConfig);
    }

    private void loadGameTimeUtility() { // TODO
        GameTimeUtility.init(this); // TODO
    }

    private void loadAPI() { // TODO
        Map<World, WorldState> worldStates = new HashMap<>(); // TODO
        for (World world : Bukkit.getWorlds()) { // TODO
            worldStates.put(world, new WorldState(world, new HashMap<>(), List.of(EventType.NIGHTMARE_NIGHT))); // TODO
        } // TODO
        WorldStateManager worldStateManager = new WorldStateManager(worldStates); // TODO
        EventAPI.initialise(worldStateManager); // TODO
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
