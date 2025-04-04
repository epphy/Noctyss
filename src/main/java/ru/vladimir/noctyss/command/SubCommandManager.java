package ru.vladimir.noctyss.command;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

abstract class SubCommandManager {
    private final List<SubCommandWrapper> wrappers = new ArrayList<>();

    protected void addSubCommand(SubCommand command, String alias, Permission permission) {
        wrappers.add(new SubCommandWrapper(command, new String[] {alias}, permission));
    }

    protected SubCommandWrapper getWrapper(String argument) {
        for (final SubCommandWrapper wrapper : wrappers) {
            for (final String alias : wrapper.aliases()) {
                if (alias.equalsIgnoreCase(argument)) return wrapper;
            }
        }
        return null;
    }

    protected List<String> getFirstAliases(CommandSender sender) {
        final List<String> firstAliases = new ArrayList<>();
        for (final SubCommandWrapper wrapper : wrappers) {
            if (!wrapper.hasPermission(sender)) continue;
            firstAliases.addAll(Arrays.asList(wrapper.aliases()));
        }
        return List.copyOf(firstAliases);
    }

    protected boolean hasAnyPermission(CommandSender sender) {
        for (final SubCommandWrapper wrapper : wrappers) {
            if (wrapper.hasPermission(sender)) return true;
        }
        return false;
    }
}
