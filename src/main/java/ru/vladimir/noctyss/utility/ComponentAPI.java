package ru.vladimir.noctyss.utility;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.regex.Pattern;

@UtilityClass
public class ComponentAPI {

    private static final Pattern LEGACY_PATTERN = Pattern.compile("&([0-9a-fA-Fk-oK-OrR])");
    private static final Pattern HEX_PATTERN = Pattern.compile("<#([0-9A-Fa-f]{6})>");
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    public static Component parseColor(String message) {
        LoggerUtility.warn("ComponentAPI", "%s".formatted(message));

        // Convert legacy codes &x to MiniMessage format
        message = LEGACY_PATTERN.matcher(message).replaceAll(result -> {
            String code = result.group(1).toLowerCase();
            return switch (code) {
                case "l" -> "<bold>";
                case "o" -> "<italic>";
                case "n" -> "<underlined>";
                case "m" -> "<strikethrough>";
                case "k" -> "<obfuscated>";
                case "r" -> "<reset>";
                default -> getColorFromCode(code);
            };
        });
        LoggerUtility.warn("ComponentAPI", "%s".formatted(message));

        // Convert hex codes {#xxxxxx} to MiniMessage format
        message = HEX_PATTERN.matcher(message).replaceAll("<color:#$1>");
        LoggerUtility.warn("ComponentAPI", "%s".formatted(message));

        return MINI_MESSAGE.deserialize(message);
    }

    private static String getColorFromCode(String code) {
        return switch (code) {
            case "0" -> "<black>"; // Black
            case "1" -> "<dark_blue>"; // Dark Blue
            case "2" -> "<dark_green>"; // Dark Green
            case "3" -> "<dark_aqua>"; // Dark Aqua
            case "4" -> "<dark_red>"; // Dark Red
            case "5" -> "<dark_purple>"; // Dark Purple
            case "6" -> "<gold>"; // Gold
            case "7" -> "<gray>"; // Gray
            case "8" -> "<dark_gray>"; // Dark Gray
            case "9" -> "<blue>"; // Blue
            case "a" -> "<green>"; // Green
            case "b" -> "<aqua>"; // Aqua
            case "c" -> "<red>"; // Red
            case "d" -> "<light_purple>"; // Light Purple
            case "e" -> "<yellow>"; // Yellow
            case "f" -> "<white>"; // White
            default -> "<white>";  // Fallback to white
        };
    }
}
