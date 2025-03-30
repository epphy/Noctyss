package ru.vladimir.noctyss.event.modules.sounds;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.event.Controllable;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.List;
import java.util.Set;

final class AmbientSoundBlocker extends PacketAdapter implements SoundManager, Controllable {
    private static final long DELAY = 0L;
    private final World world;
    private final List<Sound> allowedSounds;
    private final Set<Sound> disallowedSounds;
    private final Sound rewindSound;
    private final long stopFrequency;
    private int taskId = -1;

    AmbientSoundBlocker(JavaPlugin plugin, World world, List<Sound> allowedSounds, Set<Sound> disallowedSounds,
                        Sound rewindSound, long stopFrequency, PacketType... types) {
        super(plugin, types);
        this.world = world;
        this.allowedSounds = allowedSounds;
        this.disallowedSounds = disallowedSounds;
        this.rewindSound = rewindSound;
        this.stopFrequency = stopFrequency;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        final Player player = event.getPlayer();
        if (player == null) return;

        if (!player.getWorld().equals(world)) return;
        player.sendMessage("Sent new sound packet");

        final PacketContainer packet = event.getPacket();
        final Sound sound = packet.getSoundEffects().read(0);
        if (sound == null || allowedSounds.contains(sound)) {
            player.sendMessage("The sent sound is allowed: %s".formatted(sound));
            return;
        }

        event.setCancelled(true);
        stopDisallowedSounds();
    }

    @Override
    public void start() {
        stopAllSounds();
        taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(
                plugin, this::stopDisallowedSounds, DELAY, stopFrequency).getTaskId();
    }

    private void stopAllSounds() {
        LoggerUtility.info(this, "Stopping all sounds");
        Bukkit.getScheduler().runTask(plugin, () -> world.getPlayers().forEach(Player::stopAllSounds));
    }

    private void stopDisallowedSounds() {
        LoggerUtility.info(this, "Stopping disallowed sounds");
        for (final Player player : world.getPlayers()) {
            for (final Sound disallowedSound : disallowedSounds) {
                Bukkit.getScheduler().runTask(plugin, () -> player.stopSound(disallowedSound));
            }
        }
    }

    @Override
    public void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            stopAllSounds();
            playRewindSound();
        }
    }

    private void playRewindSound() {
        for (final Player player : world.getPlayers()) {
            player.playSound(player, rewindSound, 1.0f, 1.0f);
        }
    }
}
