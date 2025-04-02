package ru.vladimir.noctyss.utility;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Utility class for managing and interacting with time-related operations in a Minecraft world.
 * This class provides methods to check the current time of day, set the world's time, and perform
 * smooth time transitions dynamically.
 * <p>
 * All methods operate on the concept of ticks, where 1 day in Minecraft consists of 24000 ticks.
 * Additional constants define specific time ranges for various parts of the day.
 */
@UtilityClass
public class GameTimeUtility {

    private static final String CLASS_NAME = "GameTimeUtility";
    private static final long FULL_DAY_DURATION_TICKS = 24000L;
    private static final long DAY_END_TIME_TICKS = 10000L;
    private static final long NIGHT_START_TIME_TICKS = 13000L;
    private static final long[] MIDNIGHT_TICKS_TIME_RANGE = {17500L, 18500L};
    private static final long DYNAMIC_TIME_CHANGE_TICKS = 5L;

    /**
     * Checks if it is daytime in the given world.
     *
     * @param world the {@code World} to check the time of day
     * @return {@code true} if it is day, {@code false} if it is night
     */
    public static boolean isDay(@NonNull World world) {
        return world.getTime() <= DAY_END_TIME_TICKS;
    }

    /**
     * Checks if it is nighttime in the given world.
     *
     * @param world the {@code World} to check the time of day
     * @return {@code true} if it is night, {@code false} if it is day
     */
    public static boolean isNight(@NonNull World world) {
        return world.getTime() >= NIGHT_START_TIME_TICKS;
    }

    /**
     * Checks if it is midnight in the given world.
     *
     * @param world the {@code World} to check the time of day
     * @return {@code true} if the time is within the defined midnight range, otherwise {@code false}
     */
    public static boolean isMidnight(@NonNull World world) {
        long worldTime = world.getTime();
        return worldTime >= MIDNIGHT_TICKS_TIME_RANGE[0] && worldTime <= MIDNIGHT_TICKS_TIME_RANGE[1];
    }

    /**
     * Sets the time of the world to a specific tick.
     * This method calculates the world's updated time based on the requested time.
     *
     * @param world the {@code World} whose time is to be updated
     * @param newTime the new time in ticks to set in the world
     */
    public static void setTime(@NonNull World world, long newTime) {
        long updatedWorldTime = getUpdatedTime(world, newTime);
        TaskUtil.runTask(() -> world.setFullTime(updatedWorldTime));
    }

    /**
     * Dynamically adjusts the time of the world over a given duration.
     * This method ensures the transition occurs smoothly with small time increments.
     *
     * @param world the {@code World} whose time is to be transitioned
     * @param newTime the target time in ticks to transition to
     * @param duration the duration in ticks over which the transition should happen
     */
    public static void setTimeDynamically(@NonNull World world, long newTime, long duration) {
        long worldTime = world.getTime();
        long difference = newTime - worldTime;

        if (duration < DYNAMIC_TIME_CHANGE_TICKS) {
            LoggerUtility.warn(CLASS_NAME, "Failed to change time dynamically. Duration: %d is too short."
                    .formatted(duration));
            return;
        }

        long timeChange = Math.abs(difference / (duration / DYNAMIC_TIME_CHANGE_TICKS));

        final AtomicLong elapsedTime = new AtomicLong();
        final AtomicLong timeChangeAccumulator = new AtomicLong();
        final AtomicInteger taskId = new AtomicInteger();

        taskId.set(TaskUtil.runTaskTimer(() -> {
            setTime(world, worldTime + timeChangeAccumulator.addAndGet(timeChange));
            if (elapsedTime.addAndGet(DYNAMIC_TIME_CHANGE_TICKS) >= duration) {
                setTime(world, newTime);
                Bukkit.getScheduler().cancelTask(taskId.get());
            }
        }, 0L, DYNAMIC_TIME_CHANGE_TICKS).getTaskId());
    }

    /**
     * Calculates the adjusted time for the world based on the current time and the new requested time.
     * This ensures that the time remains consistent with the world's day cycle.
     *
     * @param world the {@code World} whose time is being adjusted
     * @param newTime the requested time in ticks
     * @return the adjusted full time in ticks
     */
    private static long getUpdatedTime(@NonNull World world, long newTime) {
        long worldFullTime = world.getFullTime();
        long dayNumber = worldFullTime / FULL_DAY_DURATION_TICKS;
        return dayNumber * FULL_DAY_DURATION_TICKS + newTime;
    }

    /**
     * Gets the current day number in the world based on the world's full time.
     *
     * @param world the {@code World} to get the current day number from
     * @return the current day number
     */
    public static long getDay(@NonNull World world) {
        return world.getFullTime() / FULL_DAY_DURATION_TICKS;
    }
}
