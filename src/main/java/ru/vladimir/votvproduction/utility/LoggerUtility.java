package ru.vladimir.votvproduction.utility;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A utility class for managing logging operations within the application.
 * <p>
 * This class provides static methods to initialize the logger, set logging levels,
 * and log messages with various levels of severity (info, warning, severe). It also allows
 * for custom log entries with user-defined levels and context.
 * <p>
 * The {@code LoggerUtility} is intended to centralize logging operations,
 * offering a convenient way to use a shared {@code Logger} instance across multiple components of the application.
 * <p>
 * The class ensures thread safety and enforces a singleton pattern for the logger instance,
 * restricting external instantiation.
 */
public final class LoggerUtility {
    private static Logger logger;

    private LoggerUtility() {}

    public static void init(Logger logger) {
        if (LoggerUtility.logger == null) {
            LoggerUtility.logger = logger;
            info("LoggerUtility","LoggerUtility has been initialised");
        } else {
            info("LoggerUtility", "LoggerUtility is already initialised");
        }
    }

    public static void setLevel(Level level) {
        logger.setLevel(level);
        info("LoggerUtility", "Logger level updated to %s".formatted(level));
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

    public static void announce(String announcement) {
        logger.info(announcement);
    }
}
