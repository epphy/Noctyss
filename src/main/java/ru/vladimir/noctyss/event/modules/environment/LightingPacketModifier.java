package ru.vladimir.noctyss.event.modules.environment;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedLevelChunkData;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

final class LightingPacketModifier extends PacketAdapter implements EnvironmentModifier {
    private static final PacketType PACKET_TYPE = PacketType.Play.Server.LIGHT_UPDATE;
    private final JavaPlugin pluginInstance;
    private final World world;
    private StructureModifier<WrappedLevelChunkData.LightData> lightDataStructure;
    private WrappedLevelChunkData.LightData cachedLightData;

    LightingPacketModifier(JavaPlugin pluginInstance, World world) {
        super(pluginInstance, ListenerPriority.HIGH, PACKET_TYPE);
        this.pluginInstance = pluginInstance;
        this.world = world;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        event.getPlayer().sendMessage("Sent");
        if (isWrongWorld(event)) return;
        
        lightDataStructure = event.getPacket().getLightUpdateData();
        if (lightDataStructure.size() < 1) return;

        cachedLightData = lightDataStructure.read(0);
        if (cachedLightData == null) return;

        dimLight(event);
        event.getPacket().getLightUpdateData().write(0, cachedLightData);
    }

    private void dimLight(PacketEvent event) {
        fillMasks();
        clearEmptyMasks();
        setLightLevel();
    }

    private void fillMasks() {
        final var light = getCachedLightData();
        if (light == null) {
            LoggerUtility.warn(this, "Failed to receive light packet");
            return;
        }

        final BitSet blockMask = new BitSet();
        blockMask.set(0, 18);
        light.setBlockYMask(blockMask);

        final BitSet skyMask = new BitSet();
        skyMask.set(0, 18);
        light.setSkyYMask(skyMask);
    }

    private void clearEmptyMasks() {
        final var light = getCachedLightData();
        if (light == null) {
            LoggerUtility.warn(this, "Failed to receive light packet");
            return;
        }

        light.setEmptyBlockYMask(new BitSet());
        light.setEmptySkyYMask(new BitSet());
    }

    private void setLightLevel() {
        final var light = getCachedLightData();
        if (light == null) {
            LoggerUtility.warn(this, "Failed to receive light packet");
            return;
        }

        final byte[] bytes = new byte[2048];
        Arrays.fill(bytes, (byte) 0x00);
        final List<byte[]> updates = new ArrayList<>();
        for (int i = 0; i < 18; i++) {
            updates.add(bytes);
        }
        List<byte[]> blocks = light.getBlockUpdates();
        blocks.clear();
        blocks.addAll(updates);

        List<byte[]> skies = light.getSkyUpdates();
        skies.clear();
        skies.addAll(updates);
    }

    private boolean isWrongWorld(PacketEvent event) {
        return !event.getPlayer().getWorld().equals(world);
    }

    @Nullable
    private WrappedLevelChunkData.LightData getCachedLightData() {
        if (cachedLightData != null) return cachedLightData;
        if (lightDataStructure.size() < 1) return null;
        cachedLightData = lightDataStructure.read(0);
        return cachedLightData;
    }
}
