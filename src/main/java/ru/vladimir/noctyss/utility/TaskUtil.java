package ru.vladimir.noctyss.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

/**
 * A singleton class for managing and scheduling tasks in a Bukkit environment. This
 * class provides methods for scheduling synchronous and asynchronous tasks, as
 * well as tasks with delays and repeating tasks, using the Bukkit scheduler.
 * <p>
 * TaskUtil also supports the handling of a shutting-down state to ensure
 * graceful task execution when the server or plugin is in the process of stopping.
 * <p>
 * This class is designed to operate as a singleton. Its methods provide integration
 * with the {@link org.bukkit.scheduler.BukkitScheduler}, and it relies on a valid
 * {@link JavaPlugin} instance for task scheduling.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TaskUtil {

    private static final String CLASS_NAME = TaskUtil.class.getSimpleName();
    private static TaskUtil taskUtil;
    private boolean shuttingDown;

    /**
     * Provides a singleton instance of the {@code TaskUtil} class.
     * If an instance does not already exist, this method will create and initialize it.
     *
     * @return the singleton instance of {@code TaskUtil}
     */
    public static TaskUtil getInstance() {
        if (taskUtil == null) {
            taskUtil = new TaskUtil();
            taskUtil.setShuttingDown(false);
        }
        return taskUtil;
    }

    /**
     * Unloads the TaskUtil by clearing the internal plugin reference.
     */
    public static void unload() {
        taskUtil = null;
    }

    /**
     * Updates the shutting down state for the plugin.
     *
     * @param value a boolean value indicating the new state of the plugin.
     *              When {@code true}, the plugin is marked as shutting down.
     *              When {@code false}, the plugin is marked as operational.
     */
    public void setShuttingDown(boolean value) {
        shuttingDown = value;
        LoggerUtility.info(CLASS_NAME, "Plugin state is marked as: %s"
                .formatted((shuttingDown ? "disabling" : "working")));
    }

    /**
     * Schedules a sync task using provided plugin instance. Runs without scheduler
     * if the server is marked as shutting down.
     *
     * @param plugin the {@link JavaPlugin} for the bukkit scheduler
     * @param task the {@link Runnable} task to be executed
     * @return a {@link BukkitTask} representing the scheduled task if the task is scheduled;
     *         or {@code null} if the task is executed immediately because the plugin is shutting down.
     * @throws IllegalStateException if the TaskUtil has not been initialized using the {@code init} method.
     */
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

    /**
     * Schedules an async task. Runs without scheduler if the server is marked as shutting down.
     *
     * @param plugin the {@link JavaPlugin} for the bukkit scheduler
     * @param task the {@link Runnable} task to be executed asynchronously.
     * @return a {@link BukkitTask} representing the scheduled task if the task is scheduled
     *         asynchronously, or {@code null} if the task is executed immediately because
     *         the plugin is shutting down.
     * @throws IllegalStateException if the TaskUtil has not been initialized using the {@code init} method.
     */
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

    /**
     * Schedules a repeating synchronous task with the specified delay and period.
     * If the server is marked as shutting down, the task will be executed immediately
     * without using the scheduler.
     *
     * @param plugin the {@link JavaPlugin} for the bukkit scheduler
     * @param task   the {@link Runnable} task to be executed periodically.
     * @param delay  the delay in ticks before the task is executed for the first time.
     * @param period the period in ticks between consecutive executions of the task.
     * @return a {@link BukkitTask} representing the scheduled task if the task
     *         is scheduled with the scheduler, or {@code null} if the task is executed
     *         immediately because the plugin is shutting down.
     * @throws IllegalStateException if the TaskUtil has not been properly initialized using the {@code init} method.
     */
    @Nullable
    public BukkitTask runTaskTimer(@NonNull JavaPlugin plugin, @NonNull Runnable task, long delay, long period) {
        if (shuttingDown) {
            LoggerUtility.warn(CLASS_NAME, "Failed to schedule a timer task. Running without any scheduler now");
            task.run();
            return null;
        } else {
            return Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period);
        }
    }

    /**
     * Schedules a repeating asynchronous task with the specified delay and period.
     * If the server is marked as shutting down, the task will be executed
     * immediately without using the scheduler.
     *
     * @param plugin the {@link JavaPlugin} for the bukkit scheduler
     * @param task   the {@link Runnable} task to be executed asynchronously at a fixed interval.
     * @param delay  the delay in ticks before the task is executed for the first time.
     * @param period the period in ticks between consecutive executions of the task.
     * @return a {@link BukkitTask} representing the scheduled asynchronous task if the task
     *         is successfully scheduled, or {@code null} if the task is executed immediately
     *         because the plugin is shutting down.
     * @throws IllegalStateException if the TaskUtil has not been properly initialized using the {@code init} method.
     */
    @Nullable
    public BukkitTask runTaskTimerAsync(@NonNull JavaPlugin plugin, @NonNull Runnable task, long delay, long period) {
        if (shuttingDown) {
            LoggerUtility.warn(CLASS_NAME, "Failed to schedule an async timer task. Running without any scheduler now");
            task.run();
            return null;
        } else {
            return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delay, period);
        }
    }

    /**
     * Schedules a synchronous task to be executed after a specified delay. If the plugin is
     * shutting down, the task will be executed immediately without using the scheduler.
     *
     * @param plugin the {@link JavaPlugin} for the bukkit scheduler
     * @param task  the {@link Runnable} task to be executed after the delay
     * @param delay the delay in ticks before the task is executed
     * @return a {@link BukkitTask} representing the scheduled task if it is successfully scheduled,
     *         or {@code null} if the task is executed immediately because the plugin is shutting down
     * @throws IllegalStateException if the TaskUtil has not been initialized using the {@code init} method
     */
    @Nullable
    public BukkitTask runDelayedTask(@NonNull JavaPlugin plugin, @NonNull Runnable task, long delay) {
        if (shuttingDown) {
            LoggerUtility.warn(CLASS_NAME, "Failed to schedule a delayed task. Running without any scheduler now");
            task.run();
            return null;
        } else {
            return Bukkit.getScheduler().runTaskLater(plugin, task, delay);
        }
    }

    /**
     * Schedules an asynchronous task to be executed after a specified delay. If the plugin
     * is shutting down, the task will be executed immediately without using the scheduler.
     *
     * @param plugin the {@link JavaPlugin} for the bukkit scheduler
     * @param task the {@link Runnable} task to be executed asynchronously after the delay
     * @param delay the delay in ticks before the task is executed
     * @return a {@link BukkitTask} representing the scheduled task if it is successfully scheduled
     *         asynchronously, or {@code null} if the task is executed immediately because the
     *         plugin is shutting down
     * @throws IllegalStateException if the TaskUtil has not been initialized using the {@code init} method
     */
    @Nullable
    public BukkitTask runDelayedTaskAsync(@NonNull JavaPlugin plugin, @NonNull Runnable task, long delay) {
        if (shuttingDown) {
            LoggerUtility.warn(CLASS_NAME, "Failed to schedule an async delayed task. Running without any scheduler now");
            task.run();
            return null;
        } else {
            return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delay);
        }
    }
}
