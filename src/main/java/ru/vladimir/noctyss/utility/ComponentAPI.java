package ru.vladimir.noctyss.utility;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

@UtilityClass
public class ComponentAPI {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    public static Component parseColor(String message) {
        // Convert legacy codes (&) to MiniMessage format
        message = message.replaceAll("&([0-9a-fA-Fk-oK-OrR])", "<$1>");

        // Convert hex codes {#xxxxxx} to MiniMessage format
        message = message.replaceAll("<#([0-9A-Fa-f]{6})>", "<color:#$1>");

        return MINI_MESSAGE.deserialize(message);
    }
}
