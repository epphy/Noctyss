package ru.vladimir.noctyss.event.modules.sounds;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.plugin.Plugin;
import ru.vladimir.noctyss.event.Controllable;
import ru.vladimir.noctyss.utility.LoggerUtility;

class SoundMuter extends PacketAdapter implements SoundManager, Listener, Controllable {
    private static final long DELAY = 0L;
    private static final long FREQUENCY = 20L;
    private final World world;
    private int taskId = -1;

    SoundMuter(Plugin plugin, World world, PacketType... types) {
        super(plugin, types);
        this.world = world;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        LoggerUtility.info(this, "Packet has been sent");
        if (!event.getPlayer().getWorld().equals(world)) return;
        event.setCancelled(true);
        stopAllSounds();
    }

    @EventHandler
    private void on(NotePlayEvent event) {
        if (!event.getBlock().getWorld().equals(world)) return;
        stopAllSounds();
    }

    @Override
    public void start() {
        taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(
                plugin, this::stopAllSounds, DELAY, FREQUENCY).getTaskId();
    }

    private void stopAllSounds() {
        LoggerUtility.info(this, "Stopping all sounds");
        for (final Player player : world.getPlayers()) {
            player.stopSound(SoundCategory.MUSIC);
            player.stopSound(SoundCategory.RECORDS);
        }
    }

    @Override
    public void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            playRewindSound();
            LoggerUtility.info(this, "Stopped");
        }
    }

    private void playRewindSound() {
        for (final Player player : world.getPlayers()) {
            player.playSound(player, Sound.UI_TOAST_IN, 1.0f, 1.0f);
        }
    }
}
