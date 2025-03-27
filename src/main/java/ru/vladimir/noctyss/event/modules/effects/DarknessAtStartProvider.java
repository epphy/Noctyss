package ru.vladimir.noctyss.event.modules.effects;

import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.vladimir.noctyss.event.Controllable;

@RequiredArgsConstructor
final class DarknessAtStartProvider implements EffectManager, Controllable {
    private static final int EXTRA_DELAY = 60;
    private final World world;
    private final int duration;

    @Override
    public void start() {
        for (final Player player : world.getPlayers()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, duration + EXTRA_DELAY, 0));
        }
    }

    @Override
    public void stop() {
        // Stop logic is not expected
    }
}
