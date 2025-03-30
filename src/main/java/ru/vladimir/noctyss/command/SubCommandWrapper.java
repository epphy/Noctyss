package ru.vladimir.noctyss.command;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

public record SubCommandWrapper(SubCommand command, String[] aliases, Permission permission) {
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(permission);
    }
}
