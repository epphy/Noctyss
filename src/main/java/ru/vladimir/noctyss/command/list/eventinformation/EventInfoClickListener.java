package ru.vladimir.noctyss.command.list.eventinformation;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

@RequiredArgsConstructor
public final class EventInfoClickListener implements Listener {
    private final EventInfoInventory eventInfoInventory;

    @EventHandler
    private void on(InventoryClickEvent event) {
        if (isEventInfoInventory(event.getClickedInventory()))
            event.setCancelled(true);
    }

    @EventHandler
    private void on(InventoryDragEvent event) {
        if (isEventInfoInventory(event.getInventory()))
            event.setCancelled(true);
    }

    @EventHandler
    private void on(InventoryMoveItemEvent event) {
        if (isEventInfoInventory(event.getSource()))
            event.setCancelled(true);
    }

    private boolean isEventInfoInventory(Inventory inventory) {
        if (inventory == null) return false;
        final InventoryHolder inventoryHolder = inventory.getHolder();
        return inventoryHolder != null && inventoryHolder.equals(eventInfoInventory.getHolder());
    }
}
