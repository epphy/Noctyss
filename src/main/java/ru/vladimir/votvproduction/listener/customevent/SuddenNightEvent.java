package ru.vladimir.votvproduction.listener.customevent;

import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.event.world.WorldEvent;
import org.jetbrains.annotations.NotNull;

public class SuddenNightEvent extends WorldEvent {
    private static final HandlerList handlers = new HandlerList();

    public SuddenNightEvent(@NotNull World world) {
        super(world);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
