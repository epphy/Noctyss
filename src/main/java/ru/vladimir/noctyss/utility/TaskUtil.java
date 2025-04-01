package ru.vladimir.noctyss.utility;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@UtilityClass
public class TaskUtil {
    private static final String CLASS_NAME = "TaskUtil";
    private static final boolean IS_ASYNC = false;
    private static JavaPlugin plugin;
    private static boolean shuttingDown;

    public static void init(JavaPlugin plugin) {
        if (TaskUtil.plugin == null) {
            TaskUtil.plugin = plugin;
            setShuttingDown(false);
        LoggerUtility.info(CLASS_NAME, "Initialised");
        } else {
            LoggerUtility.info(CLASS_NAME, "Already initialised");
        }
    }

    public static void setShuttingDown(boolean shuttingDown) {
        TaskUtil.shuttingDown = shuttingDown;
        LoggerUtility.info(CLASS_NAME, "Plugin state is marked as: %s"
                .formatted((shuttingDown ? "disabling" : "working")));
    }

    public static void runDelayedTask(Runnable task, long delay) {
        if (shuttingDown || plugin == null) {
            Bukkit.getScheduler().runTaskLater(plugin, task, delay);
        } else {
            LoggerUtility.warn(CLASS_NAME, "Failed to schedule a delayed task. Running now");
            runTask(task);
        }
    }

    public static void runTask(Runnable task) {
        if (shuttingDown || plugin == null || !IS_ASYNC) {
            task.run();
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    public static void runTask(JavaPlugin plugin, Runnable task) {
        if (shuttingDown || plugin == null || !IS_ASYNC) {
            task.run();
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }
}
