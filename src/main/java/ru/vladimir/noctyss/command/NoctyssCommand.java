package ru.vladimir.noctyss.command;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.vladimir.noctyss.command.list.EventListCommand;
import ru.vladimir.noctyss.config.ConfigService;

import java.util.List;

public class NoctyssCommand extends SubCommandManager implements TabExecutor {

    public void init() {
        registerCommands();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length <= 1) {
            sendFeedback(sender, ConfigService.getMessageConfig().getCommandUsage());
            return true;
        }

        final SubCommandWrapper wrapper = getWrapper(args[0]);
        if (wrapper == null) {
            sendFeedback(sender, ConfigService.getMessageConfig().getUnknownCommand());
            return true;
        }

        if (!wrapper.hasPermission(sender)) {
            sendFeedback(sender, ConfigService.getMessageConfig().getNoPermission());
            return true;
        }

        wrapper.command().onCommand(sender, args);
        return true;
    }

    private void sendFeedback(CommandSender sender, Component message) {
        sender.sendMessage(message);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1) return getFirstAliases(sender);

        final SubCommandWrapper wrapper = getWrapper(args[0]);
        if (wrapper == null) return List.of();

        if (wrapper.hasPermission(sender)) return wrapper.command().onTabComplete(sender, args);
        else return List.of();
    }

    private void registerCommands() {
        addSubCommand(
                new EventListCommand(),
                "list",
                new Permission("noctyss.eventlist")
        );
    }
}
