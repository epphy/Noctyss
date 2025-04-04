package ru.vladimir.noctyss.utility;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.vladimir.noctyss.config.ConfigService;

/**
 * A utility class for sending messages to players or command senders.
 * The messages are retrieved and formatted using specified
 * values through the message configuration.
 */
@UtilityClass
public class MessageUtil {

    /**
     * Sends a formatted message to the specified command sender.
     * The message is retrieved and formatted using the provided values.
     *
     * @param sender the command sender to whom the message will be sent
     * @param message the message component that will be formatted and sent
     * @param values the values to be used for formatting the message
     */
    public void sendMessage(@NonNull CommandSender sender, @NonNull Component message, @NonNull Object... values) {
        Component formattedMessage = ConfigService.getInstance().getMessageConfig().getMessage(message, values);
        sender.sendMessage(formattedMessage);
    }

    /**
     * Sends a formatted action bar message to the specified player.
     * The message is retrieved and formatted using the provided values.
     *
     * @param player the player to whom the action bar message will be sent
     * @param message the message component that will be formatted and sent to the player's action bar
     * @param values the values to be used for formatting the message
     */
    public void sendActionBar(@NonNull Player player, @NonNull Component message, @NonNull Object... values) {
        Component formattedMessage = ConfigService.getInstance().getMessageConfig().getMessage(message, values);
        player.sendActionBar(formattedMessage);
    }

}
