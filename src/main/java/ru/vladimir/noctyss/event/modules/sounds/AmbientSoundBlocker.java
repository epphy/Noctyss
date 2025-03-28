package ru.vladimir.noctyss.event.modules.sounds;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.vladimir.noctyss.event.Controllable;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.Set;

class AmbientSoundBlocker extends PacketAdapter implements SoundManager, Controllable {
    private static final long DELAY = 0L;
    private final World world;
    private final Set<Sound> disallowedSounds;
    private final Sound rewindSound;
    private final long stopFrequency;
    private int taskId = -1;

    AmbientSoundBlocker(Plugin plugin, World world, Set<Sound> disallowedSounds, Sound rewindSound, long stopFrequency, PacketType... types) {
        super(plugin, types);
        this.world = world;
        this.disallowedSounds = disallowedSounds;
        this.rewindSound = rewindSound;
        this.stopFrequency = stopFrequency;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (!event.getPlayer().getWorld().equals(world)) return;

        if (event.getPacket().getSoundCategories().read(0) == EnumWrappers.SoundCategory.MUSIC) {
            final Sound sound = event.getPacket().getSoundEffects().read(0);
            LoggerUtility.info(this, "Following sound called: " + sound);
            if (!disallowedSounds.contains(sound)) return;
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
        world.getPlayers().forEach(Player::stopAllSounds);
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
            LoggerUtility.info(this, "Stopped");
        }
    }

    private void playRewindSound() {
        for (final Player player : world.getPlayers()) {
            player.playSound(player, rewindSound, 1.0f, 1.0f);
        }
    }
}
