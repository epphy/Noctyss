package ru.vladimir.noctyss.command;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a wrapper around a subcommand with associated aliases and required permission.
 * This encapsulation provides utility methods to manage access control and metadata for the
 * subcommand and its aliases.
 *
 * @param command the class executor of the command
 * @param aliases the aliases of the sub command which may trigger this command
 * @param permission the command's permission
 */
 record SubCommandWrapper(SubCommand command, String[] aliases, Permission permission) {

    /**
     * Checks if the given command sender has the wrapper's permission.
     *
     * @param sender the command sender whose permissions are being checked
     * @return true if the sender has the required permission, false otherwise
     */
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
