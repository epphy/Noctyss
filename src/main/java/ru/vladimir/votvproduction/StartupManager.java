package ru.vladimir.votvproduction;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.votvproduction.utility.LoggerUtility;

import java.net.http.HttpRequest;
import java.util.List;

// TODO
final class StartupManager {
    private static final List<String> dependencies = List.of(
            "CrazyAdvancementAPI"
    );

    private StartupManager() {}

    static void checkVersion(JavaPlugin plugin) {

    }

    static void checkDependencies(JavaPlugin plugin) {
        LoggerUtility.info(StartupManager.class, "Checking for dependencies...");
        final PluginManager pluginManager = plugin.getServer().getPluginManager();
        for (final String dependency : dependencies) {
            if (!pluginManager.isPluginEnabled(dependency)) {
                handleDependencyAbsence(pluginManager, plugin);
                LoggerUtility.info(StartupManager.class, "Could not find following dependency: %s"
                        .formatted(dependency));
            }
        }
    }

    private static void handleDependencyAbsence(PluginManager pluginManager, JavaPlugin plugin) {
        LoggerUtility.info(StartupManager.class, "Disabling plugin...");
        pluginManager.disablePlugin(plugin);
    }
}
