package ru.vladimir.votvproduction.api;

import lombok.Getter;
import ru.vladimir.votvproduction.event.WorldStateManager;
import ru.vladimir.votvproduction.utility.LoggerUtility;

public class EventAPI {
    @Getter
    private static WorldStateManager worldStateManager;

    private EventAPI() {}

    public static void initialise(WorldStateManager worldStateManager) {
        if (EventAPI.worldStateManager == null) {
            EventAPI.worldStateManager = worldStateManager;
            LoggerUtility.info(EventAPI.class, "EventAPI initialised");
            return;
        }
        LoggerUtility.info(EventAPI.class, "EventAPI is already initialised");
    }
}
