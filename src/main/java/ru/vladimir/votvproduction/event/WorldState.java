package ru.vladimir.votvproduction.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class WorldState {
    private final World world;
    private final List<AbstractEvent> events;
}
