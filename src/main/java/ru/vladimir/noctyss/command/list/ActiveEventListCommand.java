package ru.vladimir.noctyss.command.list;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import ru.vladimir.noctyss.command.SubCommand;
import ru.vladimir.noctyss.config.ConfigService;

import java.util.List;

public final class ActiveEventListCommand implements SubCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length != 1) {
            Component message = ConfigService.getMessageConfig().getCommandUsage();
            sendFeedback(sender, ConfigService.getMessageConfig().getMessage(message));
        } else {
            Component message = ConfigService.getMessageConfig().getActiveEventListMsg();
            sendFeedback(sender, ConfigService.getMessageConfig().getMessage(message));
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
