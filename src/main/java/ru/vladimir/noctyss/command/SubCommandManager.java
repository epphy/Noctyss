package ru.vladimir.noctyss.command;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

abstract class SubCommandManager {
    private final List<SubCommandWrapper> subCommandWrappers = new ArrayList<>();
    protected final Permission defaultPermission = new Permission("noctyss.admin");

    protected void addSubCommand(SubCommand command, String alias, Permission permission) {
        subCommandWrappers.add(new SubCommandWrapper(command, new String[] {alias}, permission));
    }

    @Nullable
    protected SubCommandWrapper getWrapper(String argument) {
        for (final SubCommandWrapper wrapper : subCommandWrappers) {
            for (final String alias : wrapper.aliases()) {
                if (alias.equalsIgnoreCase(argument)) return wrapper;
            }
        }
        return null;
    }

    @NotNull
    protected List<String> getFirstAliases(CommandSender sender) {
        final List<String> firstAliases = new ArrayList<>();
        for (final SubCommandWrapper wrapper : subCommandWrappers) {
            if (!wrapper.hasPermission(sender)) continue;
            firstAliases.addAll(Arrays.asList(wrapper.aliases()));
        }
        return List.copyOf(firstAliases);
    }
}
