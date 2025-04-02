package ru.vladimir.noctyss.command.list;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.vladimir.noctyss.api.EventAPI;
import ru.vladimir.noctyss.command.SubCommand;
import ru.vladimir.noctyss.config.ConfigService;
import ru.vladimir.noctyss.event.EventManager;
import ru.vladimir.noctyss.event.EventType;

import java.util.List;

@RequiredArgsConstructor
public final class StopEventCommand implements SubCommand {
    private final EventManager eventManager;

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 2) handleWithoutWorld(sender, args);
        else if (args.length == 3) handleWithWorld(sender, args);
        else sendFeedback(sender, ConfigService.getMessageConfig().getCommandUsage());
    }

    private void handleWithoutWorld(CommandSender sender, String[] args) {
        if (!(sender instanceof final Player player)) {
            sendFeedback(sender, ConfigService.getMessageConfig().getPlayerOnly());
            return;
        }

        final EventType eventType = getEventType(args[1]);
        if (eventType == null) {
            sendFeedback(sender, ConfigService.getMessageConfig().getUnknownEvent());
            return;
        }

        stopEvent(player.getWorld(), eventType);
    }

    private void handleWithWorld(CommandSender sender, String[] args) {
        final EventType eventType = getEventType(args[1]);
        if (eventType == null) {
            sendFeedback(sender, ConfigService.getMessageConfig().getUnknownEvent());
            return;
        }

        final World world = Bukkit.getWorld(args[2]);
        if (world == null) {
            sendFeedback(sender, ConfigService.getMessageConfig().getUnknownWorld());
            return;
        }

        stopEvent(world, eventType);
    }

    private void stopEvent(World world, EventType eventType) {
        eventManager.stopEvent(world, eventType);
    }

    private EventType getEventType(String eventTypeName) {
        try {
            return EventType.valueOf(eventTypeName.toUpperCase().trim());
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

    private void sendFeedback(CommandSender sender, Component message) {
        sender.sendMessage(message);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return EventAPI.get
        }
    }
}
