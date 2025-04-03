package ru.vladimir.noctyss.command.list;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import ru.vladimir.noctyss.Noctyss;
import ru.vladimir.noctyss.command.SubCommand;
import ru.vladimir.noctyss.config.MessageConfig;

import java.util.List;

@RequiredArgsConstructor
public final class ReloadConfigCommand implements SubCommand {
    private final Noctyss noctyss;
    private final MessageConfig messageConfig;

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sendFeedback(sender, messageConfig.getMessage(messageConfig.getCommandUsage()));
        } else {
            noctyss.onReload();
            sendFeedback(sender, messageConfig.getMessage(messageConfig.getConfigReloaded()));
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
