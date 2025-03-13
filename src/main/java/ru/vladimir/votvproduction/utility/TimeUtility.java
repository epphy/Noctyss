package ru.vladimir.votvproduction.utility;

import org.bukkit.World;

public class TimeUtility {
    private TimeUtility() {}

    public static boolean isNight(World world) {
        final long worldTime = world.getTime();
        return worldTime >= 13000 && worldTime <= 24000;
    }
}
