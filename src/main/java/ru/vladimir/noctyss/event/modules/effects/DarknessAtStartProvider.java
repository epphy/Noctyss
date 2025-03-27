package ru.vladimir.noctyss.event.modules.effects;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.vladimir.noctyss.event.Controllable;

@RequiredArgsConstructor
final class DarknessAtStartProvider implements EffectManager, Controllable {
    private static final int EXTRA_DELAY = 60;
    private final JavaPlugin plugin;
    private final World world;
    private final int duration;

    @Override
    public void start() {
        for (final Player player : world.getPlayers()) {
            Bukkit.getScheduler().runTask(plugin, () ->
                    player.addPotionEffect(new PotionEffect(
                            PotionEffectType.DARKNESS, duration + EXTRA_DELAY, 0)));
        }
    }

    @Override
    public void stop() {
        // Stop logic is not expected
    }
}
