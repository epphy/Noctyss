package ru.vladimir.noctyss.command.list;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import ru.vladimir.noctyss.command.SubCommand;
import ru.vladimir.noctyss.config.ConfigService;

import java.util.List;

public final class EventListCommand implements SubCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sendFeedback(sender, ConfigService.getMessageConfig().getCommandUsage());
        } else {
            sendFeedback(sender, ConfigService.getMessageConfig().getEventList());
        }
    }

    private void sendFeedback(CommandSender sender, Component message) {
        sender.sendMessage(message);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
