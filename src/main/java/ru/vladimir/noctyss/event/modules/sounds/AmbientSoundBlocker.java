package ru.vladimir.noctyss.event.modules.sounds;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.event.Controllable;
import ru.vladimir.noctyss.utility.TaskUtil;

import java.util.List;
import java.util.Set;

final class AmbientSoundBlocker extends PacketAdapter implements SoundManager, Controllable {
    private static final long DELAY = 0L;
    private final JavaPlugin plugin;
    private final World world;
    private final List<Sound> allowedSounds;
    private final Set<Sound> disallowedSounds;
    private final Sound rewindSound;
    private final long stopFrequency;
    private int taskId = -1;

    AmbientSoundBlocker(JavaPlugin plugin, World world, List<Sound> allowedSounds, Set<Sound> disallowedSounds,
                        Sound rewindSound, long stopFrequency, PacketType... types) {
        super(plugin, types);
        this.plugin = plugin;
        this.world = world;
        this.allowedSounds = allowedSounds;
        this.disallowedSounds = disallowedSounds;
        this.rewindSound = rewindSound;
        this.stopFrequency = stopFrequency;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (!event.getPlayer().getWorld().equals(world)) return;

        final PacketContainer packet = event.getPacket();
        final Sound sound = packet.getSoundEffects().read(0);
        if (sound == null || allowedSounds.contains(sound)) return;

        event.setCancelled(true);
        stopDisallowedSounds();
    }

    @Override
    public void start() {
        stopAllSounds();
        taskId = Bukkit.getScheduler().runTaskTimer(
                plugin, this::stopDisallowedSounds, DELAY, stopFrequency).getTaskId();
    }

    private void stopDisallowedSounds() {
        for (final Player player : world.getPlayers()) {
            for (final Sound disallowedSound : disallowedSounds) {
                TaskUtil.getInstance().runTask(plugin, () -> {
                        player.stopSound(disallowedSound, SoundCategory.RECORDS);
                        player.stopSound(disallowedSound, SoundCategory.MUSIC);
                });
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

    private void stopAllSounds() {
        TaskUtil.getInstance().runTask(plugin, () ->
                world.getPlayers().forEach(Player::stopAllSounds));
    }

    private void playRewindSound() {
        for (final Player player : world.getPlayers()) {
            TaskUtil.getInstance().runTask(plugin, () -> player.playSound(player, rewindSound, 1.0f, 1.0f));
        }
    }
}
