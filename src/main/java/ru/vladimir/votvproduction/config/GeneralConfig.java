package ru.vladimir.votvproduction.config;

import lombok.Getter;
import org.bukkit.World;
import ru.vladimir.votvproduction.event.EventType;

import java.util.List;
import java.util.Map;

@Getter
public class GeneralConfig implements Config {
    private Map<World, List<EventType>> allowedEventWorlds;

    @Override
    public void load() {

    }

    @Override
    public void reload() {

    }
}
