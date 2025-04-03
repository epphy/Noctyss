package ru.vladimir.noctyss.utility;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A utility class for managing logging functionality across the application.
 * Provides easy-to-use methods for logging messages at various levels such as DEBUG, INFO, WARN, and ERROR.
 * Contains initialization methods to set up the logging mechanism and utility checks to ensure logging is properly configured.
 */
@UtilityClass
public class LoggerUtility {

    private final String LOGGER_NAME = "Noctyss";
    private final String LOGGER_TEMPLATE = "%s: %s";

    /**
     * Sets the logging level for the internal logger and its parent handlers.
     *
     * @param level the new logging level to be set.
     * @throws IllegalStateException if the logger has not been initialized.
     */
    public void setLevel(@NonNull Level level) {
        checkInitialized();
        Logger logger = Logger.getLogger(LOGGER_NAME);
        logger.setLevel(level);

        Handler[] handlers = logger.getParent().getHandlers();
        if (handlers.length > 0) {
            handlers[0].setLevel(level);
        }
        info("LoggerUtility", "Logger level updated to %s".formatted(level));
    }

    /**
     * Logs a debug message with the specified sender information.
     *
     * @param object the object representing the sender of the log.
     * @param debug  the debug message to be logged.
     * @throws IllegalStateException if the logger has not been initialized.
     */
    public void debug(Object object, @NonNull String debug) {
        log(object, Level.FINE, debug);
    }

    /**
     * Logs an informational message with the specified sender information.
     *
     * @param object the object representing the sender of the log.
     * @param information the informational message to be logged.
     * @throws IllegalStateException if the logger has not been initialized.
     */
    public void info(Object object, @NonNull String information) {
        log(object, Level.INFO, information);
    }

    /**
     * Logs a warning message with the specified sender information.
     *
     * @param object the object representing the sender of the log.
     * @param warning the warning message to be logged.
     * @throws IllegalStateException if the logger has not been initialized.
     */
    public void warn(Object object, @NonNull String warning) {
        log(object, Level.WARNING, warning);
    }

    /**
     * Logs an error message with the specified sender information.
     *
     * @param object the object representing the sender of the log.
     * @param error  the error message to be logged.
     * @throws IllegalStateException if the logger has not been initialized.
     */
    public void error(Object object, @NonNull String error) {
        log(object, Level.SEVERE, error);
    }

    /**
     * Announces a message by logging it at the INFO level without sender information.
     *
     * @param announcement the message to be announced.
     * @throws IllegalStateException if the LoggerUtility has not been initialized.
     */
    public void announce(@NonNull String announcement) {
        checkInitialized();
        log(Level.INFO, announcement);
    }

    /**
     * Logs a message at the specified log level with sender information.
     *
     * @param object the object representing the sender of the log.
     * @param level  the logging level at which the message should be logged.
     * @param message the message to log.
     * @throws IllegalStateException if the logger has not been initialized.
     */
    public void log(Object object, @NonNull Level level, @NonNull String message) {
        log(level, LOGGER_TEMPLATE.formatted(getSender(object), message));
    }

    /**
     * Logs a message at the specified log level.
     *
     * @param level   the logging level at which the message should be logged.
     * @param message the message to log.
     * @throws IllegalStateException if the logger has not been initialized.
     */
    private void log(@NonNull Level level, @NonNull String message) {
        checkInitialized();
        Logger.getLogger(LOGGER_NAME).log(level, message);
    }

    /**
     * Retrieves the sender's identification based on the provided object.
     * If the object is null, a default value "UnknownSender" is returned.
     * If the object is an instance of String, the object itself is returned as a string.
     * Otherwise, the class name of the object's type is returned.
     *
     * @param o the object from which the sender information is to be determined. It can be null, a String, or any other object.
     * @return a string representing the sender. It returns "UnknownSender" if the input is null, the string representation if the
     *         input is a String, or the class name of the object otherwise.
     */
    private String getSender(Object o) {
        if (o == null) return "UnknownSender";
        if (o instanceof String) return o.toString();
        return o.getClass().getSimpleName();
    }

    /**
     * Ensures that the LoggerUtility is properly initialized before performing operations that depend on it.
     * This method checks whether the underlying logger instance is set. If the logger is not initialized,
     * it throws an IllegalStateException, preventing operations that require a valid logger from executing
     * without proper setup.
     *
     * @throws IllegalStateException if the logger has not been initialized via the {@code init(Logger logger)} method.
     */
    private void checkInitialized() {
        if (Logger.getLogger(LOGGER_NAME) == null) {
            throw new IllegalStateException("LoggerUtility has not been initialized. Call init() first.");
        }
    }
}
