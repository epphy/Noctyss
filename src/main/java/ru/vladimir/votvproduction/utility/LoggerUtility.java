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
            info("Logger utility initialised");
            return;
        }
        info("Logger utility is already initialised");
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
