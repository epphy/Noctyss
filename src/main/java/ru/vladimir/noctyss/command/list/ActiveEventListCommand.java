package ru.vladimir.noctyss.command.list;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ru.vladimir.noctyss.api.EventAPI;
import ru.vladimir.noctyss.command.SubCommand;
import ru.vladimir.noctyss.config.MessageConfig;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.utility.MessageUtil;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public final class ActiveEventListCommand implements SubCommand {
    private final MessageConfig messageConfig;

    @Override
    public void onCommand(@NotNull CommandSender sender, String[] args) {
        if (args.length != 1) {
            sendFeedback(sender, messageConfig.getCommandUsage());
        } else {
            sendFeedback(sender, messageConfig.getActiveEventListMsg(), getActiveEvents());
        }
    }

    private void sendFeedback(CommandSender sender, Component message, Object... values) {
        MessageUtil.sendMessage(sender, message, values);
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, String[] args) {
        return List.of();
    }

    private String getActiveEvents() {
        final StringBuilder stringBuilder = new StringBuilder();
        final Map<World, List<EventType>> worldsWithActiveEvents = EventAPI.getActiveEventsPerWorld();

        if (worldsWithActiveEvents.isEmpty()) {
            return "none";
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
