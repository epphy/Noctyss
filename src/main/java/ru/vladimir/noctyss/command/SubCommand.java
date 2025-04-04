package ru.vladimir.noctyss.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents a subcommand that is part of a command system. Classes implementing this
 * interface define the behavior of specific subcommands, allowing for modular and
 * organized handling of command execution and tab completion.
 */
public interface SubCommand {

    /**
     * Executes the logic for a subcommand when it is invoked by a command sender.
     * This method is intended to handle the execution of a specific subcommand and process
     * the provided arguments accordingly.
     *
     * @param sender the command sender who invoked the subcommand. Typically, this could
     *               be a player, console, or command block.
     * @param args   an array of strings representing the arguments passed to the subcommand.
     *               The first element is typically the subcommand name, followed by additional
     *               arguments specific to that subcommand.
     */
    void onCommand(@NotNull CommandSender sender, @NotNull String[] args);

    /**
     * Provides a list of possible tab-completion suggestions based on the current arguments.
     *
     * @param sender the command sender requesting tab completion
     * @param args   the array of arguments typed by the sender, The first element is typically
     *               the subcommand name, followed by additional arguments specific to that subcommand.
     * @return a list of string suggestions for tab completion.
     */
    @Nullable
    List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args);
}
