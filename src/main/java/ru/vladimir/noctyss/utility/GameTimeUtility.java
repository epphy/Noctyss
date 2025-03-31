package ru.vladimir.noctyss.utility;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A utility class providing various methods for managing and manipulating in-game time
 * within a Minecraft server. This class is designed for use with the Bukkit/Spigot API.
 * <p>
 * The utility is a singleton and requires initialization with a Bukkit {@code JavaPlugin} instance.
 * <p>
 * Note:
 * - The class uses predefined constants for time-related calculations such as day duration,
 *   nighttime intervals, and midnight time range.
 * - Thread-safe mechanisms are implemented when scheduling asynchronous or task-based
 *   operations to modify the in-game time dynamically.
 */
@UtilityClass
public class GameTimeUtility {
    private static final String CLASS_NAME = "GameTimeUtility";
    private static final long DAY_DURATION_TICKS = 24000L;
    private static final long NIGHT_START_TIME_TICKS = 13000L;
    private static final long[] MIDNIGHT_TICKS_TIME_RANGE = new long[] {17500L, 18500L};
    private static final long DYNAMIC_TIME_CHANGE_TICKS = 5L;
    private static JavaPlugin plugin;

    /**
     * Initializes the {@code GameTimeUtility} with the given {@code JavaPlugin} instance.
     * Ensures that the utility is only initialized once.
     *
     * @param plugin the {@code JavaPlugin} instance required for initialization
     */
    public static void init(JavaPlugin plugin) {
        if (GameTimeUtility.plugin == null) {
            GameTimeUtility.plugin = plugin;
            LoggerUtility.info(CLASS_NAME, "GameTimeUtility has been initialised");
        } else {
            LoggerUtility.info(CLASS_NAME, "GameTimeUtility is already initialised");
        }
    }

    /**
     * Checks whether it's a day for the provided {@code world}.
     *
     * @param world the {@code World} to check the time of day
     * @return {@code true} if it is day in the specified world, otherwise {@code false}
     */
    public static boolean isDay(World world) {
        final long worldTime = world.getTime();
        return worldTime <= 10000L;
    }

    /**
     * Checks whether it's a night for the provided {@code world}.
     *
     * @param world the {@code World} to check the time of day
     * @return {@code true} if it is night in the specified world, otherwise {@code false}
     */
    public static boolean isNight(World world) {
        final long worldTime = world.getTime();
        return worldTime >= NIGHT_START_TIME_TICKS;
    }

    /**
     * Checks whether it's a midnight for the provided {@code world}.
     *
     * @param world the {@code World} whose time is to be checked
     * @return {@code true} if the current time in the world falls within the defined midnight tick range,
     *         otherwise {@code false}
     */
    public static boolean isMidnight(World world) {
        final long worldTime = world.getTime();
        return worldTime >= MIDNIGHT_TICKS_TIME_RANGE[0] && worldTime <= MIDNIGHT_TICKS_TIME_RANGE[1];
    }

    /**
     * Sets the full time of the specified {@code World} to the given {@code newTime}.
     * The time adjustment is calculated based on the world's current full time and
     * the pre-defined day duration in ticks to ensure consistency.
     *
     * @param world the {@code World} whose time is to be updated
     * @param newTime the new time in ticks to set in the specified world
     */
    public static void setTime(World world, long newTime) {
        final long updatedWorldTime = getUpdatedTime(world, newTime);
        TaskUtil.runTask(plugin, () -> world.setFullTime(updatedWorldTime));
    }

    /**
     * Dynamically adjusts the time of the specified {@code World} over a given duration.
     * The transition to the target time is spread out across the specified time interval,
     * allowing for smooth progression, controlled by the {@code DYNAMIC_TIME_CHANGE_TICKS}.
     * If the provided transition time is less than {@code DYNAMIC_TIME_CHANGE_TICKS}, the
     * operation is aborted, and a warning is logged.
     *
     * @param world the {@code World} whose time is to be transitioned dynamically
     * @param newTime the target time in ticks to transition to
     * @param time the duration in ticks over which the time transition occurs
     */
    public static void setTimeDynamically(World world, long newTime, long time) {
        final long worldTime = world.getTime();
        final long difference = newTime - worldTime;

        if (time < DYNAMIC_TIME_CHANGE_TICKS) {
            LoggerUtility.warn(CLASS_NAME,
                    "Failed to change time dynamically because provided time is less " +
                             "than the change constant. Time: %d. Constant: %d"
                                     .formatted(time, DYNAMIC_TIME_CHANGE_TICKS));
            return;
        }
        final long timeChangeChange = Math.abs(difference / (time / DYNAMIC_TIME_CHANGE_TICKS));

        final AtomicLong elapsedTime = new AtomicLong();
        final AtomicLong timeChange = new AtomicLong();
        final AtomicInteger taskId = new AtomicInteger();

        taskId.set(Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            setTime(world, worldTime + timeChange.addAndGet(timeChangeChange));
            if (elapsedTime.addAndGet(DYNAMIC_TIME_CHANGE_TICKS) >= time) {
                setTime(world, newTime);
                Bukkit.getScheduler().cancelTask(taskId.get());
            }
        }, 0L, DYNAMIC_TIME_CHANGE_TICKS).getTaskId());
    }

    /**
     * Calculates the updated full-time for the specified {@code World} using the current full time
     * and the provided {@code newTime}, ensuring it aligns with the day duration in ticks.
     *
     * @param world the {@code World} whose time is being updated
     * @param newTime the desired new time in ticks relative to the current day
     * @return the updated full-time in ticks for the specified {@code World}
     */
    private static long getUpdatedTime(World world, long newTime) {
        final long worldFullTime = world.getFullTime();
        final long dayNumber = worldFullTime / DAY_DURATION_TICKS;
        return dayNumber * DAY_DURATION_TICKS + newTime;
    }

    /**
     * Calculates the number of days have passed in {@code World} based on the world's full
     * time and the predefined duration of a day in ticks.
     *
     * @param world the {@code World} whose current day is to be determined
     * @return the current day as a {@code long} value, calculated relative to the world's full time
     */
    public static long getDay(World world) {
        return world.getFullTime() / DAY_DURATION_TICKS;
    }
}
