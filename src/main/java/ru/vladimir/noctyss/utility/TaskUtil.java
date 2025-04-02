package ru.vladimir.noctyss.utility;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class TaskUtil {

    private static final String CLASS_NAME = "TaskUtil";
    private static JavaPlugin plugin;
    private static boolean shuttingDown = false;

    public static void init(@NonNull JavaPlugin plugin) {
        if (TaskUtil.plugin == null) {
            TaskUtil.plugin = plugin;
            LoggerUtility.info(CLASS_NAME, "initialised");
        } else {
            LoggerUtility.info(CLASS_NAME, "already initialised");
        }
    }

    public static void setShuttingDown(boolean shuttingDown) {
        TaskUtil.shuttingDown = shuttingDown;
        LoggerUtility.info(CLASS_NAME, "Plugin state is marked as: %s"
                .formatted((shuttingDown ? "disabling" : "working")));
    }

    @Nullable
    public static BukkitTask runTask(@NonNull Runnable task) {
        checkInitialized();
        if (shuttingDown) {
            task.run();
            return null;
        } else {
            return Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    @Nullable
    public static BukkitTask runTask(@NonNull JavaPlugin plugin, @NonNull Runnable task) {
        if (shuttingDown) {
            LoggerUtility.warn(CLASS_NAME, "Failed to schedule a task. Running without any scheduler now");
            task.run();
            return null;
        } else {
            return Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    @Nullable
    public static BukkitTask runTaskAsync(@NonNull Runnable task) {
        checkInitialized();
        if (shuttingDown) {
            LoggerUtility.warn(CLASS_NAME, "Failed to schedule an async task. Running without any scheduler now");
            task.run();
            return null;
        } else {
            return Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        }
    }

    @Nullable
    public static BukkitTask runTaskAsync(@NonNull JavaPlugin plugin, @NonNull Runnable task) {
        if (shuttingDown) {
            LoggerUtility.warn(CLASS_NAME, "Failed to schedule an async task. Running without any scheduler now");
            task.run();
            return null;
        } else {
            return Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        }
    }

    @Nullable
    public static BukkitTask runTaskTimer(@NonNull Runnable task, long delay, long period) {
        checkInitialized();
        if (shuttingDown) {
            LoggerUtility.warn(CLASS_NAME, "Failed to schedule a timer task. Running without any scheduler now");
            task.run();
            return null;
        } else {
            return Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period);
        }
    }

    @Nullable
    public static BukkitTask runTaskTimerAsync(@NonNull Runnable task, long delay, long period) {
        checkInitialized();
        if (shuttingDown) {
            LoggerUtility.warn(CLASS_NAME, "Failed to schedule an async timer task. Running without any scheduler now");
            task.run();
            return null;
        } else {
            return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delay, period);
        }
    }

    @Nullable
    public static BukkitTask runDelayedTask(@NonNull Runnable task, long delay) {
        checkInitialized();
        if (shuttingDown) {
            LoggerUtility.warn(CLASS_NAME, "Failed to schedule a delayed task. Running without any scheduler now");
            task.run();
            return null;
        } else {
            return Bukkit.getScheduler().runTaskLater(plugin, task, delay);
        }
    }

    @Nullable
    public static BukkitTask runDelayedTaskAsync(@NonNull Runnable task, long delay) {
        checkInitialized();
        if (shuttingDown) {
            LoggerUtility.warn(CLASS_NAME, "Failed to schedule an async delayed task. Running without any scheduler now");
            task.run();
            return null;
        } else {
            return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delay);
        }
    }

    private static void checkInitialized() {
        if (plugin == null) {
            throw new IllegalStateException("TaskUtil has not been initialized. Call init() first.");
        }
    }
}
