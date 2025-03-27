package ru.vladimir.noctyss.utility;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * A utility class for managing game time-related operations in a Minecraft server environment.
 * <p>
 * This class provides methods for:
 * - Determining if the current time in a given world is night.
 * - Checking if the current time corresponds to a specific midnight range.
 * - Setting the time in a specified world to a user-defined value.
 * <p>
 * The methods in this class utilize Minecraft's internal time mechanics, where a full day cycle
 * consists of 24000 ticks.
 * <p>
 * This class depends on a JavaPlugin instance to schedule tasks, which must be initialized using
 * the {@code init(JavaPlugin plugin)} method before any other operations can be performed.
 */
@UtilityClass
public class GameTimeUtility {
    private static final long DAY_DURATION_TICKS = 24000L;
    private static final long NIGHT_START_TICKS_TIME = 13000L;
    private static final long NIGHT_END_TICKS_TIME = 24000L;
    private static final long[] MIDNIGHT_TICKS_TIME_RANGE = new long[] {17500L, 18500L};
    private static JavaPlugin plugin;

    public static void init(JavaPlugin plugin) {
        if (GameTimeUtility.plugin == null) {
            GameTimeUtility.plugin = plugin;
            LoggerUtility.info("GameTimeUtility", "GameTimeUtility has been initialised");
        } else {
            LoggerUtility.info("GameTimeUtility", "GameTimeUtility is already initialised");
        }
    }

    public static boolean isNight(World world) {
        final long worldDayTime = world.getTime();
        return worldDayTime >= NIGHT_START_TICKS_TIME && worldDayTime <= NIGHT_END_TICKS_TIME;
    }

    public static boolean isMidnight(World world) {
        final long worldDayTime = world.getTime();
        return worldDayTime >= MIDNIGHT_TICKS_TIME_RANGE[0] && worldDayTime <= MIDNIGHT_TICKS_TIME_RANGE[1];
    }

    public static void setTime(World world, long newTime) {
        final long worldFullTime = world.getFullTime();
        final long dayNumber = worldFullTime / DAY_DURATION_TICKS;
        final long updatedTime = dayNumber * DAY_DURATION_TICKS + newTime;
        Bukkit.getScheduler().runTask(plugin, () -> world.setFullTime(updatedTime));
    }
}
