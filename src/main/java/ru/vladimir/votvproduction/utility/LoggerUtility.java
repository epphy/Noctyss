package ru.vladimir.votvproduction.utility;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerUtility {
    private static Logger logger;

    private LoggerUtility() {}

    public static void init(Logger logger) {
        if (LoggerUtility.logger == null) {
            LoggerUtility.logger = logger;
        }
        LoggerUtility.info("Logger utility has been initialised");
    }

    public static void info(String information) {
        logger.info(information);
    }

    public static void warn(String warning) {
        logger.warning(warning);
    }

    public static void err(String error) {
        logger.severe(error);
    }

    public static void log(Level level, String message) {
        logger.log(level, message);
    }
}
