package ru.vladimir.noctyss.command.list.eventinformation;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.command.SubCommand;
import ru.vladimir.noctyss.config.MessageConfig;
import ru.vladimir.noctyss.utility.MessageUtil;
import ru.vladimir.noctyss.utility.TaskUtil;

import java.util.List;

public final class EventInfoCommand implements SubCommand {
    private final EventInfoInventory eventInfoInventory;
    private final EventInfoClickListener eventInfoClickListener;
    private final MessageConfig messageConfig;

    public EventInfoCommand(JavaPlugin plugin, PluginManager pluginManager, MessageConfig messageConfig) {
        eventInfoInventory = new EventInfoInventory();
        eventInfoClickListener = new EventInfoClickListener(eventInfoInventory);
        TaskUtil.getInstance().runTask(plugin, () -> pluginManager.registerEvents(eventInfoClickListener, plugin));
        this.messageConfig = messageConfig;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sendFeedback(sender, messageConfig.getCommandUsage());
            return;
        }

        if (!(sender instanceof final Player player)) {
            sendFeedback(sender, messageConfig.getPlayerOnly());
            return;
        }

        player.openInventory(eventInfoInventory.getInventory());
    }

    private void sendFeedback(CommandSender sender, Component message, Object... values) {
        MessageUtil.sendMessage(sender, message, values);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
