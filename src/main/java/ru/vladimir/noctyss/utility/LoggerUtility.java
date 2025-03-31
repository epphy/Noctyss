package ru.vladimir.noctyss.utility;

import lombok.experimental.UtilityClass;

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
@UtilityClass
public class LoggerUtility {
    private static final String CLASS_NAME = "LoggerUtility";
    private static final String LOGGER_TEMPLATE = "%s: %s";
    private static Logger logger;

    public static void init(Logger logger) {
        if (LoggerUtility.logger == null) {
            LoggerUtility.logger = logger;
            info(CLASS_NAME,"LoggerUtility has been initialised");
        } else {
            info(CLASS_NAME, "LoggerUtility is already initialised");
        }
    }

    public static void setLevel(Level level) {
        logger.setLevel(level);
        logger.getParent().getHandlers()[0].setLevel(level);
        info(CLASS_NAME, "Logger level updated to %s".formatted(level));
    }

    public static void debug(Object object, String debug) {
        logger.fine(LOGGER_TEMPLATE.formatted(getSender(object), debug));
    }

    public static void info(Object object, String information) {
        logger.info(LOGGER_TEMPLATE.formatted(getSender(object), information));
    }

    public static void warn(Object object, String warning) {
        logger.warning(LOGGER_TEMPLATE.formatted(getSender(object), warning));
    }

    public static void err(Object object, String error) {
        logger.severe(LOGGER_TEMPLATE.formatted(getSender(object), error));
    }

    public static void log(Object object, Level level, String message) {
        logger.log(level, LOGGER_TEMPLATE.formatted(getSender(object), message));
    }

    public static void announce(String announcement) {
        logger.info(announcement);
    }

    private static String getSender(Object o) {
        if (o == null) return "UnknownSender";
        if (o instanceof String) return o.toString();
        return o.getClass().getSimpleName();
    }
}
