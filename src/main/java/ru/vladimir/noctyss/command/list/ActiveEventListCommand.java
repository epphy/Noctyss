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
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class ActiveEventListCommand implements SubCommand {
    private final MessageConfig messageConfig;

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        sendFeedback(sender, messageConfig.getActiveEventListMsg(), getActiveEvents());
    }

    private void sendFeedback(CommandSender sender, Component message, Object... values) {
        MessageUtil.sendMessage(sender, message, values);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        return List.of();
    }

    private String getActiveEvents() {
        final StringBuilder stringBuilder = new StringBuilder();
        final Set<Map.Entry<World, List<EventType>>> worldsWithActiveEventsEntries = EventAPI.getActiveEventsPerWorldEntries();

        if (worldsWithActiveEventsEntries.isEmpty()) {
            return "none";
        }

        for (final Map.Entry<World, List<EventType>> worldWithActiveEvents : worldsWithActiveEventsEntries) {

            final String worldName = worldWithActiveEvents.getKey().getName();
            final String eventNames = worldWithActiveEvents.getValue().stream()
                    .map(Enum::name)
                    .map(String::toLowerCase)
                    .collect(Collectors.joining(", "));

            stringBuilder
                    .append("\n")
                    .append(worldName)
                    .append(": ")
                    .append(eventNames);
        }

        return stringBuilder.toString();
    }
}
