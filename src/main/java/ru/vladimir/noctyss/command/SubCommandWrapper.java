package ru.vladimir.noctyss.command;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import java.util.Arrays;
import java.util.Objects;

record SubCommandWrapper(SubCommand command, String[] aliases, Permission permission) {
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(permission);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SubCommandWrapper that = (SubCommandWrapper) o;
        return Objects.deepEquals(aliases, that.aliases) && Objects.equals(command, that.command) && Objects.equals(permission, that.permission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(command, Arrays.hashCode(aliases), permission);
    }

    @Override
    public String toString() {
        return "SubCommandWrapper{" +
                "command=" + command +
                ", aliases=" + Arrays.toString(aliases) +
                ", permission=" + permission +
                '}';
    }
}
