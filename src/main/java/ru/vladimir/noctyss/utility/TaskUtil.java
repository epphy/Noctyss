package ru.vladimir.noctyss.utility;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for managing and scheduling various types of tasks in a Bukkit/Spigot plugin environment.
 * Provides a centralized and reliable mechanism to handle synchronous and asynchronous task execution,
 * ensuring correct behavior during server shutdown and plugin lifecycle changes.
 */
@UtilityClass
public class TaskUtil {

    private final String CLASS_NAME = "TaskUtil";
    private JavaPlugin plugin;
    private boolean shuttingDown = false;

    /**
     * Initializes the TaskUtil with the specified plugin instance.
     *
     * @param plugin the {@link JavaPlugin} instance used to initialize TaskUtil.
     */
    public void init(@NonNull JavaPlugin plugin) {
        if (TaskUtil.plugin == null) {
            TaskUtil.plugin = plugin;
            LoggerUtility.info(CLASS_NAME, "initialised");
        } else {
            LoggerUtility.info(CLASS_NAME, "already initialised");
        }
    }

    /**
     * Unloads the TaskUtil by clearing the internal plugin reference.
     */
    public void unload() {
        plugin = null;
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
     * Schedules a sync task. Runs without scheduler if the server is marked as shutting down.
     *
     * @param task the {@link Runnable} task to be executed.
     * @return a {@link BukkitTask} representing the scheduled task if the task is scheduled;
     *         or {@code null} if the task is executed immediately because the plugin is shutting down.
     * @throws IllegalStateException if the TaskUtil has not been initialized using the {@code init} method.
     */
    @Nullable
    public BukkitTask runTask(@NonNull Runnable task) {
        checkInitialized();
        if (shuttingDown) {
            LoggerUtility.info(CLASS_NAME, "Failed to schedule a task. Running without any scheduler now");
            task.run();
            return null;
        } else {
            return Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    /**
     * Schedules a sync task using provided plugin instance. Runs without scheduler
     * if the server is marked as shutting down. Scheduled for removal because it
     * has its own plugin instance, and we do not need to provide one.
     *
     * @param task the {@link Runnable} task to be executed.
     * @return a {@link BukkitTask} representing the scheduled task if the task is scheduled;
     *         or {@code null} if the task is executed immediately because the plugin is shutting down.
     * @throws IllegalStateException if the TaskUtil has not been initialized using the {@code init} method.
     */
    @Deprecated(forRemoval = true)
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
     * @param task the {@link Runnable} task to be executed asynchronously.
     * @return a {@link BukkitTask} representing the scheduled task if the task is scheduled
     *         asynchronously, or {@code null} if the task is executed immediately because
     *         the plugin is shutting down.
     * @throws IllegalStateException if the TaskUtil has not been initialized using the {@code init} method.
     */
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

    /**
     * Schedules an async task using provided plugin instance. Runs without scheduler
     * if the server is marked as shutting down. Scheduled for removal because it
     * has its own plugin instance, and we do not need to provide one.
     *
     * @param plugin the {@link JavaPlugin} instance used to schedule the task.
     * @param task the {@link Runnable} task to be executed asynchronously.
     * @return a {@link BukkitTask} representing the scheduled asynchronous task,
     *         or {@code null} if the task is executed immediately because the plugin is shutting down.
     * @throws IllegalStateException if the TaskUtil has not been properly initialized using the {@code init} method.
     */
    @Deprecated(forRemoval = true)
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
     * @param task   the {@link Runnable} task to be executed periodically.
     * @param delay  the delay in ticks before the task is executed for the first time.
     * @param period the period in ticks between consecutive executions of the task.
     * @return a {@link BukkitTask} representing the scheduled task if the task
     *         is scheduled with the scheduler, or {@code null} if the task is executed
     *         immediately because the plugin is shutting down.
     * @throws IllegalStateException if the TaskUtil has not been properly initialized using the {@code init} method.
     */
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

    /**
     * Schedules a repeating asynchronous task with the specified delay and period.
     * If the server is marked as shutting down, the task will be executed
     * immediately without using the scheduler.
     *
     * @param task   the {@link Runnable} task to be executed asynchronously at a fixed interval.
     * @param delay  the delay in ticks before the task is executed for the first time.
     * @param period the period in ticks between consecutive executions of the task.
     * @return a {@link BukkitTask} representing the scheduled asynchronous task if the task
     *         is successfully scheduled, or {@code null} if the task is executed immediately
     *         because the plugin is shutting down.
     * @throws IllegalStateException if the TaskUtil has not been properly initialized using the {@code init} method.
     */
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

    /**
     * Schedules a synchronous task to be executed after a specified delay. If the plugin is
     * shutting down, the task will be executed immediately without using the scheduler.
     *
     * @param task  the {@link Runnable} task to be executed after the delay
     * @param delay the delay in ticks before the task is executed
     * @return a {@link BukkitTask} representing the scheduled task if it is successfully scheduled,
     *         or {@code null} if the task is executed immediately because the plugin is shutting down
     * @throws IllegalStateException if the TaskUtil has not been initialized using the {@code init} method
     */
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

    /**
     * Schedules an asynchronous task to be executed after a specified delay. If the plugin
     * is shutting down, the task will be executed immediately without using the scheduler.
     *
     * @param task the {@link Runnable} task to be executed asynchronously after the delay
     * @param delay the delay in ticks before the task is executed
     * @return a {@link BukkitTask} representing the scheduled task if it is successfully scheduled
     *         asynchronously, or {@code null} if the task is executed immediately because the
     *         plugin is shutting down
     * @throws IllegalStateException if the TaskUtil has not been initialized using the {@code init} method
     */
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

    /**
     * Ensures that the TaskUtil instance has been properly initialized with a valid plugin reference.
     * Throws an {@link IllegalStateException} if the initialization has not been performed by calling {@code init()}.
     * This method is used internally to validate the state before performing operations that depend on the plugin.
     *
     * @throws IllegalStateException if the {@link TaskUtil} instance has not been initialized.
     */
    private void checkInitialized() {
        if (plugin == null) {
            throw new IllegalStateException("TaskUtil has not been initialized. Call init() first.");
        }
    }
}
