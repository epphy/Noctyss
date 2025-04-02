package ru.vladimir.noctyss.api.events.nightmarenight;

import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.event.world.WorldEvent;
import org.jetbrains.annotations.NotNull;
import ru.vladimir.noctyss.api.events.ICustomEvent;

public final class NightmareNightEndEvent extends WorldEvent implements ICustomEvent {
    private static final HandlerList handlers = new HandlerList();

    public NightmareNightEndEvent(@NotNull World world, boolean isAsync) {
        super(world, isAsync);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }
}
