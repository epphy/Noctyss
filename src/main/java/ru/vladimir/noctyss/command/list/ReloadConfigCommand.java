package ru.vladimir.noctyss.command.list;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import ru.vladimir.noctyss.command.SubCommand;
import ru.vladimir.noctyss.config.ConfigService;
import ru.vladimir.noctyss.event.GlobalEventScheduler;

import java.util.List;

@RequiredArgsConstructor
public final class ReloadConfigCommand implements SubCommand {
    private final GlobalEventScheduler globalEventScheduler;

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length != 1) {
            Component message = ConfigService.getMessageConfig().getCommandUsage();
            sendFeedback(sender, ConfigService.getMessageConfig().getMessage(message));
        } else {
            ConfigService.reload(globalEventScheduler);
            Component message = ConfigService.getMessageConfig().getConfigReloaded();
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
