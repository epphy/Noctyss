package ru.vladimir.noctyss.event.modules.environment;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedLevelChunkData;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.List;

final class LightDimmer extends PacketAdapter implements EnvironmentModifier {
    private static final PacketType packetType = PacketType.Play.Server.LIGHT_UPDATE;
    private final JavaPlugin plugin;
    private final World world;

    LightDimmer(Plugin plugin, World world, PacketType... types) {
        super(plugin, packetType);
        this.plugin = (JavaPlugin) plugin;
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
        final StructureModifier<WrappedLevelChunkData.LightData> lightDataStructureModifier = packet.getLightUpdateData();
        final List<WrappedLevelChunkData.LightData> lightDataList = lightDataStructureModifier.getValues();
        final WrappedLevelChunkData.LightData lightData = packet.getLightUpdateData().read(0);

        LoggerUtility.info(this, "Size: %d".formatted(lightDataList.size()));
        LoggerUtility.info(this, "");

        LoggerUtility.info(this, "{SkyYMask=%s, BlockYMask=%s, EmptySkyYMask=%s, EmptyBlockYMask=%s, SkyUpdates=%s, BlockUpdates=%s}"
                .formatted(lightData.getSkyYMask(), lightData.getBlockYMask(), lightData.getEmptySkyYMask(), lightData.getEmptyBlockYMask(),
                        lightData.getSkyUpdates(), lightData.getBlockUpdates()));
        LoggerUtility.info(this, "");
        LoggerUtility.info(this, "");
        LoggerUtility.info(this, "TEST TIME!");

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            //
        }, 200L);
    }

    private boolean isWrongWorld(PacketEvent event) {
        return !event.getPlayer().getWorld().equals(world);
    }
}
