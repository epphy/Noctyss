package ru.vladimir.votvproduction.event;

import lombok.Getter;
import ru.vladimir.votvproduction.event.types.EventInstance;
import ru.vladimir.votvproduction.event.types.nightmarenight.NightmareNightInstance;

import java.util.function.Supplier;

@Getter
public enum EventType {
    NIGHTMARE_NIGHT(NightmareNightInstance::new);

    private final Supplier<EventInstance> eventSupplier;

    EventType(Supplier<EventInstance> eventSupplier) {
        this.eventSupplier = eventSupplier;
    }
}
