package ru.vladimir.noctyss.utility;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class TaskUtil {

    private final String CLASS_NAME = "TaskUtil";
    private JavaPlugin plugin;
    private boolean shuttingDown = false;

    public void init(@NonNull JavaPlugin plugin) {
        if (TaskUtil.plugin == null) {
            TaskUtil.plugin = plugin;
            LoggerUtility.info(CLASS_NAME, "initialised");
        } else {
            LoggerUtility.info(CLASS_NAME, "already initialised");
        }
    }

    public void setShuttingDown(boolean shuttingDown) {
        TaskUtil.shuttingDown = shuttingDown;
        LoggerUtility.info(CLASS_NAME, "Plugin state is marked as: %s"
                .formatted((shuttingDown ? "disabling" : "working")));
    }

    @Nullable
    public BukkitTask runTask(@NonNull Runnable task) {
        checkInitialized();
        if (shuttingDown) {
            task.run();
            return null;
        } else {
            return Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    @Nullable
    public BukkitTask runTask(@NonNull JavaPlugin plugin, @NonNull Runnable task) {
        if (shuttingDown) {
            LoggerUtility.warn(CLASS_NAME, "Failed to schedule a task. Running without any scheduler now");
            task.run();
            return null;
        } else {
            return Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    @Nullable
    public BukkitTask runTaskAsync(@NonNull Runnable task) {
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
    public BukkitTask runTaskAsync(@NonNull JavaPlugin plugin, @NonNull Runnable task) {
        if (shuttingDown) {
            LoggerUtility.warn(CLASS_NAME, "Failed to schedule an async task. Running without any scheduler now");
            task.run();
            return null;
        } else {
            return Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        }
    }

    @Nullable
    public BukkitTask runTaskTimer(@NonNull Runnable task, long delay, long period) {
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
    public BukkitTask runTaskTimerAsync(@NonNull Runnable task, long delay, long period) {
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
    public BukkitTask runDelayedTask(@NonNull Runnable task, long delay) {
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
    public BukkitTask runDelayedTaskAsync(@NonNull Runnable task, long delay) {
        checkInitialized();
        if (shuttingDown) {
            LoggerUtility.warn(CLASS_NAME, "Failed to schedule an async delayed task. Running without any scheduler now");
            task.run();
            return null;
        } else {
            return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delay);
        }
    }

    private void checkInitialized() {
        if (plugin == null) {
            throw new IllegalStateException("TaskUtil has not been initialized. Call init() first.");
        }
    }
}
