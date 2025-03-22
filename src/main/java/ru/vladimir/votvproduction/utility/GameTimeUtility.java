package ru.vladimir.votvproduction.utility;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public final class GameTimeUtility {
    private static final long DAY_DURATION_TICKS = 24000L;
    private static final long NIGHT_START_TICKS_TIME = 13000L;
    private static final long NIGHT_END_TICKS_TIME = 24000L;
    private static final long[] MIDNIGHT_TICKS_TIME_RANGE = new long[] {17500L, 18500L};
    private static JavaPlugin plugin;

    public static void init(JavaPlugin plugin) {
        if (GameTimeUtility.plugin == null) {
            GameTimeUtility.plugin = plugin;
            LoggerUtility.info(GameTimeUtility.class, "GameTimeUtility has been initialised");
        } else {
            LoggerUtility.info(GameTimeUtility.class, "GameTimeUtility is already initialised");
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
