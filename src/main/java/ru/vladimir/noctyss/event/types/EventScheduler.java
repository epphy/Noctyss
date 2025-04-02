package ru.vladimir.noctyss.event.types;

import org.bukkit.World;
import ru.vladimir.noctyss.event.Controllable;

public interface EventScheduler extends Controllable {
    void startEvent(World world);
}
