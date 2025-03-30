package ru.vladimir.noctyss.event.modules.environment;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.event.modules.environment.light.LightUpdateWrapperProxy;

final class LightingPacketModifier extends PacketAdapter implements EnvironmentModifier {
    private static final PacketType[] PACKET_TYPE = new PacketType[]
            {PacketType.Play.Server.LIGHT_UPDATE, PacketType.Play.Server.MAP_CHUNK};
    private final World world;

    LightingPacketModifier(JavaPlugin pluginInstance, World world) {
        super(pluginInstance, ListenerPriority.HIGH, PACKET_TYPE);
        this.world = world;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (isWrongWorld(event)) return;
        
        final var packet = event.getPacket();
        final var proxy = new LightUpdateWrapperProxy(packet.getLightUpdateData());
        if (!proxy.containsLightData()) return;

        proxy.fillMasks();
        proxy.clearEmptyMasks();
        proxy.setLightLevel((byte) 0x00);
    }

    private boolean isWrongWorld(PacketEvent event) {
        return !event.getPlayer().getWorld().equals(world);
    }
}
