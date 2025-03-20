package ru.vladimir.votvproduction.utility;

import org.bukkit.World;

public class GameTimeUtility {
    private static final long NIGHT_START_TICKS_TIME = 13000L;
    private static final long NIGHT_END_TICKS_TIME = 24000L;

    public static boolean isNight(World world) {
        final long worldDayTime = world.getTime();
        return worldDayTime >= NIGHT_START_TICKS_TIME && worldDayTime <= NIGHT_END_TICKS_TIME;
    }
}
