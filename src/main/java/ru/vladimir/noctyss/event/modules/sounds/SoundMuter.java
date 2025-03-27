package ru.vladimir.noctyss.event.modules.sounds;

import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import ru.vladimir.noctyss.event.Controllable;

@RequiredArgsConstructor
public class SoundMuter implements SoundManager, Controllable {
    private static final long DELAY = 0L;
    private static final long FREQUENCY = 20L;
    private final World world;

    @Override
    public void start() {

    }

    private void processPlayers() {
        for (final Player player : world.getPlayers()) {
            if (player.isDead()) continue;
            if (player.getGameMode() == GameMode.SPECTATOR) continue;

            player.stopAllSounds();
        }
    }

    @Override
    public void stop() {

    }
}
