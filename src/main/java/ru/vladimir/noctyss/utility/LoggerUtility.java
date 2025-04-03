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

    private final String CLASS_NAME = "LoggerUtility";
    private final String LOGGER_TEMPLATE = "%s: %s";
    private Logger logger;

    public void init(@NonNull Logger logger) {
        if (LoggerUtility.logger == null) {
            LoggerUtility.logger = logger;
            info(CLASS_NAME, "initialised");
        } else {
            info(CLASS_NAME, "already initialised");
        }
    }

    public void setLevel(@NonNull Level level) {
        checkInitialized();
        logger.setLevel(level);

        if (logger.getParent().getHandlers().length > 0) {
            logger.getParent().getHandlers()[0].setLevel(level);
        }
        info(CLASS_NAME, "Logger level updated to %s".formatted(level));
    }

    public void debug(Object object, String debug) {
        log(object, Level.FINE, debug);
    }

    public void info(Object object, String information) {
        log(object, Level.INFO, information);
    }

    public void warn(Object object, String warning) {
        log(object, Level.WARNING, warning);
    }

    public void error(Object object, String error) {
        log(object, Level.SEVERE, error);
    }

    public void announce(String announcement) {
        checkInitialized();
        log(Level.INFO, announcement);
    }

    public void log(Object object, @NonNull Level level, String message) {
        log(level, LOGGER_TEMPLATE.formatted(getSender(object), message));
    }

    private void log(@NonNull Level level, String message) {
        checkInitialized();
        logger.log(level, message);
    }

    private String getSender(Object o) {
        if (o == null) return "UnknownSender";
        if (o instanceof String) return o.toString();
        return o.getClass().getSimpleName();
    }

    private void checkInitialized() {
        if (logger == null) {
            throw new IllegalStateException("LoggerUtility has not been initialized. Call init() first.");
        }
    }
}
