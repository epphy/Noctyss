package ru.vladimir.noctyss.utility;

import lombok.Setter;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@UtilityClass
public class TaskUtil {
    private static JavaPlugin plugin;
    @Setter
    private static boolean shuttingDown;

    public static void init(JavaPlugin plugin) {
        if (TaskUtil.plugin == null) {
            TaskUtil.plugin = plugin;
            TaskUtil.shuttingDown = false;
            LoggerUtility.info("TaskUtil", "Initialised");
        } else {
            LoggerUtility.info("TaskUtil", "Already initialised");
        }
    }

    public static void runTask(Runnable task) {
        if (!shuttingDown || plugin == null) {
            task.run();
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    public static void runTask(JavaPlugin plugin, Runnable task) {
        if (!shuttingDown || plugin == null) {
            task.run();
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }
}
