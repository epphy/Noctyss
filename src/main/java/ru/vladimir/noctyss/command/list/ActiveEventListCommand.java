package ru.vladimir.noctyss.command.list;

import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import ru.vladimir.noctyss.api.EventAPI;
import ru.vladimir.noctyss.command.SubCommand;
import ru.vladimir.noctyss.config.ConfigService;
import ru.vladimir.noctyss.event.EventType;

import java.util.List;
import java.util.Map;

public final class ActiveEventListCommand implements SubCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length != 1) {
            Component message = ConfigService.getMessageConfig().getCommandUsage();
            sendFeedback(sender, ConfigService.getMessageConfig().getMessage(message));
        } else {
            Component message = ConfigService.getMessageConfig().getActiveEventListMsg();
            sendFeedback(sender, ConfigService.getMessageConfig().getMessage(message, getActiveEvents()));
        }
    }

    private void sendFeedback(CommandSender sender, Component message) {
        sender.sendMessage(message);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    private String getActiveEvents() {
        final StringBuilder stringBuilder = new StringBuilder();
        final Map<World, List<EventType>> worldsWithActiveEvents = EventAPI.getActiveEventsPerWorld();

        if (worldsWithActiveEvents.isEmpty()) {
            return "No active events yet";
        }

        for (final Map.Entry<World, List<EventType>> worldWithActiveEvents : EventAPI.getActiveEventsPerWorldEntries()) {

            final String worldName = worldWithActiveEvents.getKey().getName();
            final List<String> eventNames = worldWithActiveEvents.getValue().stream()
                    .map(Enum::name)
                    .toList();

            stringBuilder
                    .append("\n")
                    .append(worldName)
                    .append(": ")
                    .append(eventNames);
        }

        return stringBuilder.toString();
    }
}
