package ru.vladimir.votvproduction.utility;

import org.bukkit.World;

public final class GameTimeUtility {
    private static final long DAY_DURATION_TICKS = 24000L;
    private static final long NIGHT_START_TICKS_TIME = 13000L;
    private static final long NIGHT_END_TICKS_TIME = 24000L;

    public static boolean isNight(World world) {
        final long worldDayTime = world.getTime();
        return worldDayTime >= NIGHT_START_TICKS_TIME && worldDayTime <= NIGHT_END_TICKS_TIME;
    }

    public static void setTime(World world, long newTime) {
        final long worldFullTime = world.getFullTime();
        final long dayNumber = worldFullTime / DAY_DURATION_TICKS;
        final long updatedTime = dayNumber * DAY_DURATION_TICKS + newTime;
        world.setFullTime(updatedTime);
    }
}
