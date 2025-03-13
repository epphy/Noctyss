package ru.vladimir.votvproduction.utility;

import java.util.Random;

public class RandomNumberUtility {
    private static final int DEFAULT_CHANCE_RANGE = 100;
    private static final Random random = new Random();

    private RandomNumberUtility() {}

    public static boolean isWithinChance(int bound, int maxTrueValue) {
        return random.nextInt(bound) <= maxTrueValue;
    }

    public static boolean isWithinChance(int maxTrueValue) {
        return random.nextInt(DEFAULT_CHANCE_RANGE) <= maxTrueValue;
    }
}
