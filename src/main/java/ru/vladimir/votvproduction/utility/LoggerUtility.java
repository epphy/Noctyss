package ru.vladimir.votvproduction.utility;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A utility class for centralized logging. This class provides static methods
 * for logging different levels of messages, including info, warning, error,
 * and custom log levels. It uses a shared Logger instance that must be
 * initialized before use.
 */
public class LoggerUtility {
    private static Logger logger;

    private LoggerUtility() {}

    public static void init(Logger logger) {
        if (LoggerUtility.logger == null) {
            LoggerUtility.logger = logger;
            info(LoggerUtility.class,"Logger utility initialised");
            return;
        }
        info(LoggerUtility.class, "Logger utility is already initialised");
    }

    public static void setLevel(Level level) {
        logger.setLevel(level);
        info(LoggerUtility.class, "Logger level updated to %s".formatted(level));
    }

    public static void info(Object object, String information) {
        logger.info("%s: %s".formatted(object.getClass().getSimpleName(), information));
    }

    public static void warn(Object object, String warning) {
        logger.warning("%s: %s".formatted(object.getClass().getSimpleName(), warning));
    }

    public static void err(Object object, String error) {
        logger.severe("%s: %s".formatted(object.getClass().getSimpleName(), error));
    }

    public static void log(Object object, Level level, String message) {
        logger.log(level, "%s: %s".formatted(object.getClass().getSimpleName(), message));
    }
}
