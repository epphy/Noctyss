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
    private static final int INVENTORY_SIZE = 27;
    private static final Component INVENTORY_TITLE = Component.text("Noctyss: Events Information");

    private static final Component INFORMATION_ICON_NAME = MiniMessage.miniMessage().deserialize(
            "<white>Information</white>");
    private static final List<Component> INFORMATION_ICON_LORE = List.of(
            MiniMessage.miniMessage().deserialize(" "),
            MiniMessage.miniMessage().deserialize("<gray> In addition to all events, </gray>"),
            MiniMessage.miniMessage().deserialize("<gray> you may also setup some of </gray>"),
            MiniMessage.miniMessage().deserialize("<gray> their configs. </gray>"),
            MiniMessage.miniMessage().deserialize(" "),
            MiniMessage.miniMessage().deserialize("<gray> All events share a notification </gray>"),
            MiniMessage.miniMessage().deserialize("<gray> config, where you may setup </gray>"),
            MiniMessage.miniMessage().deserialize("<gray> what it says at specific periods </gray>"),
            MiniMessage.miniMessage().deserialize("<gray> of events. </gray>"),
            MiniMessage.miniMessage().deserialize(" "),
            MiniMessage.miniMessage().deserialize("<gray> Lastly, you cannot sleep during </gray>"),
            MiniMessage.miniMessage().deserialize("<gray> any of the events. </gray>"),
            MiniMessage.miniMessage().deserialize(" "),
            MiniMessage.miniMessage().deserialize("<gray> For more help, please refer to </gray>"),
            MiniMessage.miniMessage().deserialize("<gray> the plugin's documentation. </gray>"),
            MiniMessage.miniMessage().deserialize(" ")
    );

    private static final Component NIGHTMARE_NIGHT_ICON_NAME = MiniMessage.miniMessage().deserialize(
            "<gradient:#660000:#222222>Nightmare Night</gradient>");
    private static final List<Component> NIGHTMARE_NIGHT_ICON_LORE = List.of(
            MiniMessage.miniMessage().deserialize(" "),
            MiniMessage.miniMessage().deserialize("<gray> This event occurs randomly </gray>"),
            MiniMessage.miniMessage().deserialize("<gray> at nights, accompanied by: </gray>"),
            MiniMessage.miniMessage().deserialize(" "),
            MiniMessage.miniMessage().deserialize("<white> - longer night </white>"),
            MiniMessage.miniMessage().deserialize("<white> - random creepy sounds </white>"),
            MiniMessage.miniMessage().deserialize("<white> - effect of darkness </white>"),
            MiniMessage.miniMessage().deserialize("<white> - modified spawnrate </white>"),
            MiniMessage.miniMessage().deserialize(" ")
    );

    private static final Component SUDDEN_NIGHT_ICON_NAME = MiniMessage.miniMessage().deserialize(
            "<gradient:#F69806:#F68406>Sudden Night</gradient>");
    private static final List<Component> SUDDEN_NIGHT_ICON_LORE = List.of(
            MiniMessage.miniMessage().deserialize(" "),
            MiniMessage.miniMessage().deserialize("<gray> This event occurs randomly </gray>"),
            MiniMessage.miniMessage().deserialize("<gray> at days, accompanied by: </gray>"),
            MiniMessage.miniMessage().deserialize(" "),
            MiniMessage.miniMessage().deserialize("<white> - change in light </white>"),
            MiniMessage.miniMessage().deserialize("<white> - complete silence </white>"),
            MiniMessage.miniMessage().deserialize("<white> - background soundtrack </white>"),
            MiniMessage.miniMessage().deserialize("<white> - freeze of all entities </white>"),
            MiniMessage.miniMessage().deserialize("<white> - disabled entity spawn </white>"),
            MiniMessage.miniMessage().deserialize(" ")
    );

    private final EventInfoInventoryHolder holder;
    private final Inventory inventory;

    public EventInfoInventory() {
        holder = new EventInfoInventoryHolder();
        inventory = Bukkit.createInventory(holder, INVENTORY_SIZE, INVENTORY_TITLE);
        setupDesign();
        setupIcons();
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

    private void setupIcons() {
        final ItemStack informationIcon = new ItemStack(Material.PAPER);
        final ItemMeta informationIconMeta = informationIcon.getItemMeta();
        informationIconMeta.displayName(INFORMATION_ICON_NAME);
        informationIconMeta.lore(INFORMATION_ICON_LORE);
        informationIcon.setItemMeta(informationIconMeta);

        final ItemStack nightmareNightIcon = new ItemStack(Material.BLACK_CONCRETE);
        final ItemMeta nightmareNightIconMeta = nightmareNightIcon.getItemMeta();
        nightmareNightIconMeta.displayName(NIGHTMARE_NIGHT_ICON_NAME);
        nightmareNightIconMeta.lore(NIGHTMARE_NIGHT_ICON_LORE);
        nightmareNightIcon.setItemMeta(nightmareNightIconMeta);

        final ItemStack suddenNightIcon = new ItemStack(Material.CLOCK);
        final ItemMeta suddenNightIconMeta = suddenNightIcon.getItemMeta();
        suddenNightIconMeta.displayName(SUDDEN_NIGHT_ICON_NAME);
        suddenNightIconMeta.lore(SUDDEN_NIGHT_ICON_LORE);
        suddenNightIcon.setItemMeta(suddenNightIconMeta);

        inventory.setItem(4, informationIcon);
        inventory.setItem(10, nightmareNightIcon);
        inventory.setItem(11, suddenNightIcon);
    }

    class EventInfoInventoryHolder implements InventoryHolder {
        @Override
        public @NotNull Inventory getInventory() {
            return inventory;
        }
    }
}
