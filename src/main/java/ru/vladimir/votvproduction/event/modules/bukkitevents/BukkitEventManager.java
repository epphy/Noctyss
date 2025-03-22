package ru.vladimir.votvproduction.event.modules.bukkitevents;

import ru.vladimir.votvproduction.event.modules.Module;

public class BukkitEventManager implements Module {
    /*

    TODO: This class using its builder is going to add necessary bukkit events
          in its list before user executes start and registering all events in
          the plugin manager. On command stop, all events should be unregistered.

     */

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    public static class Builder {

    }
}
