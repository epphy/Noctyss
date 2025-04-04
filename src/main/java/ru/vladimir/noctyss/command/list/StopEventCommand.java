package ru.vladimir.noctyss.command.list;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import ru.vladimir.noctyss.api.EventAPI;
import ru.vladimir.noctyss.command.SubCommand;
import ru.vladimir.noctyss.config.MessageConfig;
import ru.vladimir.noctyss.event.EventManager;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.utility.MessageUtil;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public final class StopEventCommand implements SubCommand {
    private final EventManager eventManager;
    private final MessageConfig messageConfig;

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 2) handleWithoutWorld(sender, args);
        else if (args.length == 3) handleWithWorld(sender, args);
        else sendFeedback(sender, messageConfig.getCommandUsage());
    }

    private void handleWithoutWorld(CommandSender sender, String[] args) {
        if (!(sender instanceof final Player player)) {
            sendFeedback(sender, messageConfig.getPlayerOnly());
            return;
        }

        final EventType eventType = getEventType(args[1]);
        if (eventType == null) {
            sendFeedback(sender, messageConfig.getUnknownEvent());
            return;
        }

        attemptStopEvent(sender, player.getWorld(), eventType);
    }

    private void handleWithWorld(CommandSender sender, String[] args) {
        final EventType eventType = getEventType(args[1]);
        if (eventType == null) {
            sendFeedback(sender, messageConfig.getUnknownEvent());
            return;
        }

        final World world = Bukkit.getWorld(args[2]);
        if (world == null) {
            sendFeedback(sender, messageConfig.getUnknownWorld());
            return;
        }

        attemptStopEvent(sender, world, eventType);
    }

    private void attemptStopEvent(CommandSender sender, World world, EventType eventType) {
        if (!EventAPI.isEventAllowed(world, eventType)) {
            sendFeedback(sender, messageConfig.getEventDisallowed(), world.getName());
            return;
        }

        if (!EventAPI.isEventActive(world ,eventType)) {
            sendFeedback(sender, messageConfig.getEventInactive(), world.getName());
            return;
        }

        eventManager.stopEvent(world, eventType);
        sendFeedback(sender, messageConfig.getEventStopped(), world.getName());
    }

    private void sendFeedback(CommandSender sender, Component message, Object... values) {
        MessageUtil.sendMessage(sender, message, values);
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
                    : EventAPI.getWorldsWithActiveEvent(eventType).stream()
                        .map(World::getName)
                        .toList();
        }

        return List.of();
    }

    @Nullable
    private EventType getEventType(String eventTypeName) {
        try {
            if (eventTypeName == null) return null;
            return EventType.valueOf(eventTypeName.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
