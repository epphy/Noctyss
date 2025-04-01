package ru.vladimir.noctyss.command.list;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.vladimir.noctyss.command.SubCommand;
import ru.vladimir.noctyss.config.ConfigService;

import java.util.List;

public final class EventInfoCommand implements SubCommand, Listener {
    private Inventory inventory;

    public void init() {
        setupInventory();
    }

    private void setupInventory() {
        inventory = Bukkit.createInventory(
                null, 27, Component.text("Noctyss: Events information"));

        setupDesign();
        final ItemStack nightmareNightIcon;
        final ItemStack suddenNightIcon;
    }

    private void setupDesign() {
        final ItemStack designLightBluePanel = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        final int[] designLightBluePanelSlots = new int[] {0, 1, 2, 6, 7, 8, 9, 17, 21, 22, 23};

        final ItemStack designGrayPanel = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        final int[] designGrayPanelSlots = new int[] {4, 5, 6, 18, 19, 20, 24, 25, 26};

        final ItemMeta designMeta = designGrayPanel.getItemMeta();
        designMeta.displayName(Component.empty());

        designLightBluePanel.setItemMeta(designMeta);
        designGrayPanel.setItemMeta(designMeta);

        for (int i : designLightBluePanelSlots) {
            inventory.setItem(i, designLightBluePanel);
        }

        for (int i : designGrayPanelSlots) {
            inventory.setItem(i, designGrayPanel);
        }
    }

    private void setupEventIcons() {

    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sendFeedback(sender, ConfigService.getMessageConfig().getCommandUsage());
            return;
        }

        if (!(sender instanceof final Player player)) {
            sender.sendMessage(ConfigService.getMessageConfig().getPlayerOnly());
            return;
        }

        player.openInventory(inventory);
    }

    private void sendFeedback(CommandSender sender, Component message) {
        sender.sendMessage(message);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    @EventHandler
    private void on(InventoryClickEvent event) {

    }

    // Interaction is not allowed;
    // Inventory has a unique id;
    // Inventory has a needed design
}
