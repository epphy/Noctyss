package ru.vladimir.votvproduction.config;

import lombok.Getter;
import org.bukkit.World;

import java.util.List;

@Getter
public class NightmareNightConfig implements Config {
    private List<World> allowedWorlds;
    private int eventChance;

    @Override
    public void load() {

    }

    @Override
    public void reload() {

    }
}
