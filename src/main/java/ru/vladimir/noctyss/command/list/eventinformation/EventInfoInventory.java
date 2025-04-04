package ru.vladimir.noctyss.command.list.eventinformation;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public class EventInfoInventory {
    // Dependency
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    // Inventory: Main
    private static final Component INVENTORY_TITLE = Component.text("Noctyss: Events Information");
    private static final int INVENTORY_SIZE = 27;

    // Inventory: Information
    private static final Component INFORMATION_ICON_NAME = MINI_MESSAGE.deserialize("<!i><white>Information</white>");
    private static final List<Component> INFORMATION_ICON_LORE = List.of(
            MINI_MESSAGE.deserialize(" "),
            MINI_MESSAGE.deserialize("<!i><gray> Learn about all <aqua>events</aqua> and how to configure them. </gray>"),
            MINI_MESSAGE.deserialize(" "),
            MINI_MESSAGE.deserialize("<!i><gray> Each event has its own <aqua>settings</aqua> and effects. </gray>"),
            MINI_MESSAGE.deserialize("<!i><gray> You can customize certain aspects to fit your world. </gray>"),
            MINI_MESSAGE.deserialize(" "),
            MINI_MESSAGE.deserialize("<!i><gray> <aqua>Notifications</aqua> will inform you of event progress. </gray>"),
            MINI_MESSAGE.deserialize("<!i><gray> Adjust what they display at different times. </gray>"),
            MINI_MESSAGE.deserialize(" "),
            MINI_MESSAGE.deserialize("<!i><gray> <aqua>Sleeping</aqua> is <aqua>disabled</aqua> during active events. </gray>"),
            MINI_MESSAGE.deserialize(" "),
            MINI_MESSAGE.deserialize("<!i><gray> For more details, check the <aqua>plugin documentation</aqua>. </gray>"),
            MINI_MESSAGE.deserialize(" ")
    );

    // Inventory: Nightmare Night
    private static final Component NIGHTMARE_NIGHT_ICON_NAME = MINI_MESSAGE.deserialize("<!i><gradient:#660000:#222222>Nightmare Night</gradient>");
    private static final List<Component> NIGHTMARE_NIGHT_ICON_LORE = List.of(
            MINI_MESSAGE.deserialize(" "),
            MINI_MESSAGE.deserialize("<!i><gray> A <dark_red>dark</dark_red> and <dark_red>ominous</dark_red> night approaches... </gray>"),
            MINI_MESSAGE.deserialize("<!i><gray> During this event, you may encounter: </gray>"),
            MINI_MESSAGE.deserialize(" "),
            MINI_MESSAGE.deserialize("<!i><white> - <dark_red>Longer</dark_red> night duration </white>"),
            MINI_MESSAGE.deserialize("<!i><white> - <dark_red>Unsettling</dark_red> ambient sounds </white>"),
            MINI_MESSAGE.deserialize("<!i><white> - <dark_red>Reduced</dark_red> vision </white>"),
            MINI_MESSAGE.deserialize("<!i><white> - <dark_red>Increased</dark_red> monster spawns </white>"),
            MINI_MESSAGE.deserialize(" "),
            MINI_MESSAGE.deserialize("<!i><gray> Be cautious... <dark_red>you're never alone</dark_red>. </gray>"),
            MINI_MESSAGE.deserialize(" ")
    );

    // Inventory: Sudden Night
    private static final Component SUDDEN_NIGHT_ICON_NAME = MINI_MESSAGE.deserialize("<!i><gradient:#F69806:#F68406>Sudden Night</gradient>");
    private static final List<Component> SUDDEN_NIGHT_ICON_LORE = List.of(
            MINI_MESSAGE.deserialize(" "),
            MINI_MESSAGE.deserialize("<!i><gray> The <gold>sun vanishes</gold>, and the world is plunged into <gold>darkness</gold>. </gray>"),
            MINI_MESSAGE.deserialize("<!i><gray> During this event, you will experience: </gray>"),
            MINI_MESSAGE.deserialize(" "),
            MINI_MESSAGE.deserialize("<!i><white> - <gold>Instant</gold> nightfall </white>"),
            MINI_MESSAGE.deserialize("<!i><white> - <gold>Complete</gold> silence </white>"),
            MINI_MESSAGE.deserialize("<!i><white> - <gold>Unsettling</gold> music </white>"),
            MINI_MESSAGE.deserialize("<!i><white> - <gold>All</gold> entities freeze </white>"),
            MINI_MESSAGE.deserialize("<!i><white> - <gold>No</gold> mobs will spawn </white>"),
            MINI_MESSAGE.deserialize(" "),
            MINI_MESSAGE.deserialize("<!i><gray> Something feels <gold>wrong</gold>... Stay alert. </gray>"),
            MINI_MESSAGE.deserialize(" ")
    );

    // Inventory
    private final EventInfoInventoryHolder holder;
    private final Inventory inventory;

    public EventInfoInventory() {
        holder = new EventInfoInventoryHolder();
        inventory = Bukkit.createInventory(holder, INVENTORY_SIZE, INVENTORY_TITLE);
        setupInventoryLayout();
        setupIcons();
    }

    private void setupInventoryLayout() {
        setDesignItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE, 0, 1, 2, 6, 7, 8, 9, 17, 21, 22, 23);
        setDesignItem(Material.GRAY_STAINED_GLASS_PANE, 4, 5, 6, 18, 19, 20, 24, 25, 26);
    }

    private void setDesignItem(Material material, int... slots) {
        final ItemStack itemStack = new ItemStack(material);
        final ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) meta.displayName(Component.empty());
        itemStack.setItemMeta(meta);

        for (final int slot : slots) {
            inventory.setItem(slot, itemStack);
        }
    }

    private void setupIcons() {
        setIcon(Material.PAPER, INFORMATION_ICON_NAME, INFORMATION_ICON_LORE, 4);
        setIcon(Material.COAL_BLOCK, NIGHTMARE_NIGHT_ICON_NAME, NIGHTMARE_NIGHT_ICON_LORE, 10);
        setIcon(Material.CLOCK, SUDDEN_NIGHT_ICON_NAME, SUDDEN_NIGHT_ICON_LORE, 11);
    }

    private void setIcon(Material material, Component displayName, List<Component> lore, int slot) {
        final ItemStack item = new ItemStack(material);
        final ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(displayName);
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        inventory.setItem(slot, item);
    }

    class EventInfoInventoryHolder implements InventoryHolder {
        @Override
        public @NotNull Inventory getInventory() {
            return inventory;
        }
    }
}
