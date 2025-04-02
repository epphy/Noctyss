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

import java.util.Arrays;
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

        attemptStopEvent(sender, player.getWorld(), eventType);
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

        attemptStopEvent(sender, world, eventType);
    }

    private void attemptStopEvent(CommandSender sender, World world, EventType eventType) {
        if (!EventAPI.isEventAllowed(world, eventType)) {
            sendFeedback(sender, ConfigService.getMessageConfig().getEventDisallowed());
            return;
        }

        if (!EventAPI.isEventActive(world ,eventType)) {
            sendFeedback(sender, ConfigService.getMessageConfig().getEventInactive());
            return;
        }

        eventManager.stopEvent(world, eventType);
        sendFeedback(sender, ConfigService.getMessageConfig().getEventStopped());
    }

    private void sendFeedback(CommandSender sender, Component message) {
        sender.sendMessage(message);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return Arrays.stream(EventType.values())
                    .map(Enum::name)
                    .toList();
        }

        if (args.length == 3) {
            final EventType eventType = getEventType(args[2]);
            return (eventType == null)
                    ? List.of()
                    : EventAPI.getWorldsWithSpecificActiveEvent(eventType).stream()
                        .map(World::getName)
                        .toList();
        }

        return List.of();
    }

    private EventType getEventType(String eventTypeName) {
        try {
            return EventType.valueOf(eventTypeName.toUpperCase().trim());
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }
}
