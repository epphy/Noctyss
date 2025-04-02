package ru.vladimir.noctyss.utility;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A utility class for managing logging functionality across the application.
 * Provides easy-to-use methods for logging messages at various levels such as DEBUG, INFO, WARN, and ERROR.
 * Contains initialization methods to set up the logging mechanism and utility checks to ensure logging is properly configured.
 */
@UtilityClass
public class LoggerUtility {

    private static final String CLASS_NAME = "LoggerUtility";
    private static final String LOGGER_TEMPLATE = "%s: %s";
    private static Logger logger;

    public static void init(@NonNull Logger logger) {
        if (LoggerUtility.logger == null) {
            LoggerUtility.logger = logger;
            info(CLASS_NAME, "initialised");
        } else {
            info(CLASS_NAME, "already initialised");
        }
    }

    public static void setLevel(@NonNull Level level) {
        checkInitialized();
        logger.setLevel(level);

        if (logger.getParent().getHandlers().length > 0) {
            logger.getParent().getHandlers()[0].setLevel(level);
        }
        info(CLASS_NAME, "Logger level updated to %s".formatted(level));
    }

    public static void debug(Object object, String debug) {
        log(object, Level.FINE, debug);
    }

    public static void info(Object object, String information) {
        log(object, Level.INFO, information);
    }

    public static void warn(Object object, String warning) {
        log(object, Level.WARNING, warning);
    }

    public static void error(Object object, String error) {
        log(object, Level.SEVERE, error);
    }

    public static void announce(String announcement) {
        checkInitialized();
        log(Level.INFO, announcement);
    }

    public static void log(Object object, @NonNull Level level, String message) {
        log(level, LOGGER_TEMPLATE.formatted(getSender(object), message));
    }

    private static void log(@NonNull Level level, String message) {
        checkInitialized();
        logger.log(level, message);
    }

    private static String getSender(Object o) {
        if (o == null) return "UnknownSender";
        if (o instanceof String) return o.toString();
        return o.getClass().getSimpleName();
    }

    private static void checkInitialized() {
        if (logger == null) {
            throw new IllegalStateException("LoggerUtility has not been initialized. Call init() first.");
        }
    }
}
