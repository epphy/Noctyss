package ru.vladimir.votvproduction.manager;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.vladimir.votvproduction.config.NightmareNightConfig;
import ru.vladimir.votvproduction.manager.customevent.NightStartEvent;
import ru.vladimir.votvproduction.utility.RandomNumberUtility;

@RequiredArgsConstructor
public class NightmareNightEventManager implements Listener {
    private NightmareNightConfig config;
    private int chance;

    public void init() {
        cache();
    }

    @EventHandler
    private void on(NightStartEvent event) {
        if (shouldStart()) {
            // Start logic
        }
    }

    private boolean shouldStart() {
        return RandomNumberUtility.isWithinChance(chance);
    }

    private void cache() {
        chance = config.getEventChance();
    }
}
