package ru.vladimir.noctyss.command.list.eventinformation;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.command.SubCommand;
import ru.vladimir.noctyss.config.ConfigService;
import ru.vladimir.noctyss.utility.TaskUtil;

import java.util.List;

public final class EventInfoCommand implements SubCommand {
    private final EventInfoInventory eventInfoInventory;
    private final EventInfoClickListener eventInfoClickListener;

    public EventInfoCommand(JavaPlugin plugin, PluginManager pluginManager) {
        eventInfoInventory = new EventInfoInventory();
        eventInfoClickListener = new EventInfoClickListener();
        TaskUtil.runTask(() -> pluginManager.registerEvents(eventInfoClickListener, plugin));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sendFeedback(sender, ConfigService.getMessageConfig().getCommandUsage());
            return;
        }

        if (!(sender instanceof final Player player)) {
            sendFeedback(sender, ConfigService.getMessageConfig().getPlayerOnly());
            return;
        }

        player.openInventory(eventInfoInventory.getInventory());
    }

    private void sendFeedback(CommandSender sender, Component message) {
        sender.sendMessage(message);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
