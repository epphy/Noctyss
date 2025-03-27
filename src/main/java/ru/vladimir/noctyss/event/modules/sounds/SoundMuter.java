package ru.vladimir.noctyss.event.modules.sounds;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

@RequiredArgsConstructor
public class SoundMuter implements SoundManager, PacketListener {
    private static final PacketType[] blockedPacketTypes = new PacketType[]
            {PacketType.Play.Server.ENTITY_SOUND, PacketType.Play.Server.NAMED_SOUND_EFFECT};
    private final JavaPlugin plugin;
    private final World world;

    @Override
    public void onPacketSending(PacketEvent event) {
        if (!event.getPlayer().getWorld().equals(world)) return;

        final PacketType eventPacketType = event.getPacketType();
        for (final PacketType blockedPacketType : blockedPacketTypes) {
            if (eventPacketType == blockedPacketType) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        final PacketType eventPacketType = event.getPacketType();
        for (final PacketType blockedPacketType : blockedPacketTypes) {
            if (eventPacketType == blockedPacketType) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @Override
    public ListeningWhitelist getSendingWhitelist() {
        return ListeningWhitelist.newBuilder().build();
    }

    @Override
    public ListeningWhitelist getReceivingWhitelist() {
        return ListeningWhitelist.newBuilder().build();
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }
}
