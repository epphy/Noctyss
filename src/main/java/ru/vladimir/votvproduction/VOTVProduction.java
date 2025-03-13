package ru.vladimir.votvproduction;

import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.votvproduction.utility.LoggerUtility;

public final class VOTVProduction extends JavaPlugin {

    /*

    STARTUP LOGIC

     */

    @Override
    public void onEnable() {
        initLogger();
        startupMessage();
    }

    private void initLogger() {
        LoggerUtility.init(getLogger());
    }

    private void startupMessage() {
        LoggerUtility.info("Welcome! Thanks for choosing us!");
    }

    /*

    SHUTDOWN LOGIC

     */

    @Override
    public void onDisable() {
        shutdownMessage();
    }

    private void shutdownMessage() {
        LoggerUtility.info("Have a nice day!");
    }
}
