package ru.vladimir.noctyss.event.modules.sounds;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.vladimir.noctyss.utility.LoggerUtility;

public class SoundMuter extends PacketAdapter implements SoundManager {
    private final World world;

    public SoundMuter(Plugin plugin, World world, PacketType... types) {
        super(plugin, types);
        this.world = world;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        LoggerUtility.info(this, "Packet has been sent");
        if (!event.getPlayer().getWorld().equals(world)) return;
        event.setCancelled(true);
        for (final Player player : world.getPlayers()) {
            player.stopAllSounds();
        }
    }
}
