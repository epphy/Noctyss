package ru.vladimir.noctyss.command.list;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ru.vladimir.noctyss.Noctyss;
import ru.vladimir.noctyss.command.SubCommand;
import ru.vladimir.noctyss.config.MessageConfig;
import ru.vladimir.noctyss.utility.MessageUtil;

import java.util.List;

@RequiredArgsConstructor
public final class ReloadConfigCommand implements SubCommand {
    private final Noctyss noctyss;
    private final MessageConfig messageConfig;

    @Override
    public void onCommand(@NotNull CommandSender sender, String[] args) {
        if (args.length != 1) {
            sendFeedback(sender, messageConfig.getCommandUsage());
        } else {
            noctyss.onReload();
            sendFeedback(sender, messageConfig.getConfigReloaded());
        }
    }

    private void sendFeedback(CommandSender sender, Component message, Object... values) {
        MessageUtil.sendMessage(sender, message, values);
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, String[] args) {
        return List.of();
    }
}
