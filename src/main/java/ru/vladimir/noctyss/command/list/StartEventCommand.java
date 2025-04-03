package ru.vladimir.noctyss.command.list;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.vladimir.noctyss.api.EventAPI;
import ru.vladimir.noctyss.command.SubCommand;
import ru.vladimir.noctyss.config.MessageConfig;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.event.GlobalEventScheduler;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public final class StartEventCommand implements SubCommand {
    private final GlobalEventScheduler globalEventScheduler;
    private final MessageConfig messageConfig;

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 2) handleWithoutWorld(sender, args);
        else if (args.length == 3) handleWithWorld(sender, args);
        else sendFeedback(sender, messageConfig.getMessage(messageConfig.getCommandUsage()));
    }

    private void handleWithoutWorld(CommandSender sender, String[] args) {
        if (!(sender instanceof final Player player)) {
            sendFeedback(sender, messageConfig.getMessage(messageConfig.getPlayerOnly()));
            return;
        }

        final EventType eventType = getEventType(args[1]);
        if (eventType == null) {
            sendFeedback(sender, messageConfig.getMessage(messageConfig.getUnknownEvent()));
            return;
        }

        attemptStartEvent(sender, player.getWorld(), eventType);
    }

    private void handleWithWorld(CommandSender sender, String[] args) {
        final EventType eventType = getEventType(args[1]);
        if (eventType == null) {
            sendFeedback(sender, messageConfig.getMessage(messageConfig.getUnknownEvent()));
            return;
        }

        final World world = Bukkit.getWorld(args[2]);
        if (world == null) {
            sendFeedback(sender, messageConfig.getMessage(messageConfig.getUnknownWorld()));
            return;
        }

        attemptStartEvent(sender, world, eventType);
    }

    private void attemptStartEvent(CommandSender sender, World world, EventType eventType) {
        if (!EventAPI.isEventAllowed(world, eventType)) {
            sendFeedback(sender, messageConfig.getMessage(messageConfig.getEventDisallowed(), world.getName()));
            return;
        }

        if (EventAPI.isEventActive(world ,eventType)) {
            sendFeedback(sender, messageConfig.getMessage(messageConfig.getEventAlreadyActive(), world.getName()));
            return;
        }

        if (EventAPI.isAnyEventActive(world)) {
            sendFeedback(sender, messageConfig.getMessage(messageConfig.getOtherEventActive(), world.getName()));
            return;
        }

        globalEventScheduler.getEventSchedulers().get(eventType).startEvent(world);
        sendFeedback(sender, messageConfig.getMessage(messageConfig.getEventStarted(), world.getName()));
    }

    private void sendFeedback(CommandSender sender, Component message) {
        sender.sendMessage(message);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return Arrays.stream(EventType.values())
                    .map(Enum::name)
                    .map(String::toLowerCase)
                    .toList();
        }

        if (args.length == 3) {
            final EventType eventType = getEventType(args[1]);
            return (eventType == null)
                    ? List.of()
                    : EventAPI.getWorldsWithoutEvent(eventType).stream()
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
