package ru.vladimir.noctyss.event.modules.environment;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedLevelChunkData;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.BitSet;

final class LightingPacketDimmer extends PacketAdapter implements EnvironmentModifier {
    private static final PacketType packetType = PacketType.Play.Server.LIGHT_UPDATE;
    private final JavaPlugin pluginInstance;
    private final World world;

    LightingPacketDimmer(JavaPlugin pluginInstance, World world) {
        super(pluginInstance, packetType);
        this.pluginInstance = pluginInstance;
        this.world = world;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (isWrongWorld(event)) return;
        LoggerUtility.info(this, "Sent");
        dimLight(event);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (isWrongWorld(event)) return;
        LoggerUtility.info(this, "Received");
        dimLight(event);
    }

    private void dimLight(PacketEvent event) {
        final PacketContainer packet = event.getPacket();
        final WrappedLevelChunkData.LightData lightData = packet.getLightUpdateData().read(0);

        final BitSet a = lightData.getBlockYMask();
        final BitSet b = lightData.getSkyYMask();

        LoggerUtility.info(this, "{SkyYMask=%s, BlockYMask=%s, EmptySkyYMask=%s, EmptyBlockYMask=%s, SkyUpdates=%s, BlockUpdates=%s}"
                .formatted(lightData.getSkyYMask(), lightData.getBlockYMask(), lightData.getEmptySkyYMask(), lightData.getEmptyBlockYMask(),
                        lightData.getSkyUpdates(), lightData.getBlockUpdates()));
        LoggerUtility.info(this, "");
        LoggerUtility.info(this, "");
        LoggerUtility.info(this, "TEST TIME!");

        Bukkit.getScheduler().runTaskLater(pluginInstance, () -> {
            LoggerUtility.info(this, "First test");
            lightData.setSkyYMask(new BitSet(0));
            lightData.setBlockYMask(new BitSet(0));
        }, 200L);

        Bukkit.getScheduler().runTaskLater(pluginInstance, () -> {
            LoggerUtility.info(this, "Last test!");
            lightData.setSkyYMask(new BitSet(100));
            lightData.setBlockYMask(new BitSet(100));
        }, 600L);

        LoggerUtility.info(this, "Setting back!");

        Bukkit.getScheduler().runTaskLater(pluginInstance, () -> {
            lightData.setSkyYMask(a);
            lightData.setBlockYMask(b);
        }, 600L);
    }

    private boolean isWrongWorld(PacketEvent event) {
        return !event.getPlayer().getWorld().equals(world);
    }
}
