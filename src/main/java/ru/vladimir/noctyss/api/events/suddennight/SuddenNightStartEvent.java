package ru.vladimir.noctyss.api.events.suddennight;

import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.event.world.WorldEvent;
import org.jetbrains.annotations.NotNull;

public class SuddenNightStartEvent extends WorldEvent {
    private static final HandlerList handlers = new HandlerList();

    public SuddenNightStartEvent(@NotNull World world, boolean isAsync) {
        super(world, isAsync);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public @NotNull HandlerList getHandlerList() {
        return handlers;
    }
}
