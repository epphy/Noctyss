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
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.event.GlobalEventScheduler;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public final class StartEventCommand implements SubCommand {
    private final GlobalEventScheduler globalEventScheduler;

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

        attemptStartEvent(sender, player.getWorld(), eventType);
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

        attemptStartEvent(sender, world, eventType);
    }

    private void attemptStartEvent(CommandSender sender, World world, EventType eventType) {
        if (!EventAPI.isEventAllowed(world, eventType)) {
            final Component message = ConfigService.getMessageConfig().getEventDisallowed();
            sendFeedback(sender, ConfigService.getMessageConfig().getMessage(message, world.getName()));
            return;
        }

        if (EventAPI.isEventActive(world ,eventType)) {
            final Component message = ConfigService.getMessageConfig().getEventAlreadyActive();
            sendFeedback(sender, ConfigService.getMessageConfig().getMessage(message, world.getName()));
            return;
        }

        globalEventScheduler.getEventSchedulers().get(eventType).startEvent(world);
        final Component message = ConfigService.getMessageConfig().getEventStarted();
        sendFeedback(sender, ConfigService.getMessageConfig().getMessage(message, world.getName()));
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
