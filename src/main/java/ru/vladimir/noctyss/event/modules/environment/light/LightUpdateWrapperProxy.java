package ru.vladimir.noctyss.event.modules.environment.light;

import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedLevelChunkData;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.*;

@RequiredArgsConstructor
class LightUpdateWrapperProxy {
    private static final String NO_CONTAINS = "Failed to process light: light packet is not found";
    private final StructureModifier<WrappedLevelChunkData.LightData> lightDataStructure;
    private WrappedLevelChunkData.LightData lightData;

    void resetLight() {
        final var light = getLightData();
        if (light == null) {
            LoggerUtility.warn(this, NO_CONTAINS);
            return;
        }

        light.getBlockUpdates().clear();
        light.getSkyUpdates().clear();

        light.setBlockYMask(new BitSet());
        light.setSkyYMask(new BitSet());
        light.setEmptyBlockYMask(new BitSet());
        light.setEmptySkyYMask(new BitSet());
    }

    void fillMasks() {
        final var light = getLightData();
        if (light == null) {
            LoggerUtility.warn(this, NO_CONTAINS);
            return;
        }

        final BitSet blockMask = new BitSet();
        blockMask.set(0, 18);
        light.setBlockYMask(blockMask);

        final BitSet skyMask = new BitSet();
        skyMask.set(0, 18);
        light.setSkyYMask(skyMask);
    }

    void clearEmptyMasks() {
        final var light = getLightData();
        if (light == null) {
            LoggerUtility.warn(this, NO_CONTAINS);
            return;
        }

        light.setEmptyBlockYMask(new BitSet());
        light.setEmptySkyYMask(new BitSet());
    }

    void setLightLevel(Map<Location, Byte> lightSources, byte level) {
        final byte[] bytes = new byte[2048];
        Arrays.fill(bytes, level);

        final List<byte[]> updates = new ArrayList<>();
        for (int i = 0; i < 18; i++) {
            updates.add(bytes);
        }

//        for (final Map.Entry<Location, Byte> entry : lightSources.entrySet()) {
//            applyLightSpread(updates, entry.getKey(), entry.getValue(), level);
//        }

        blockLightArrays(updates);
        skyLightArrays(updates);
    }

    private void applyLightSpread(List<byte[]> updates, Location source, byte initialLevel, byte minimumLevel) {
        final Queue<Location> queue = new LinkedList<>();
        final Map<Location, Byte> visited = new HashMap<>();

        queue.add(source);
        visited.put(source, initialLevel);

        while (!queue.isEmpty()) {
            final Location current = queue.poll();
            final byte lightLevel = visited.get(current);
            if (lightLevel <= minimumLevel) continue;

            for (final BlockFace face : BlockFace.values()) {
                final Location neighbour = current.clone().add(face.getModX(), face.getModY(), face.getModZ());
                if (visited.containsKey(neighbour)) continue;

                final byte newLevel = (byte) (lightLevel - 1);
                visited.put(neighbour, newLevel);
                queue.add(neighbour);
            }
        }

        for (final Map.Entry<Location, Byte> entry : visited.entrySet()) {
            int sectionIndex = getChunkSectionIndex(entry.getKey());
            updates.set(sectionIndex, new byte[] { entry.getValue() });
        }
    }

    private int getChunkSectionIndex(Location location) {
        return (location.getBlockY() >> 4) & 0xF;
    }

    void skyLightArrays(List<byte[]> array) {
        final var light = getLightData();
        if (light == null) {
            LoggerUtility.warn(this, NO_CONTAINS);
            return;
        }

        final List<byte[]> skyUpdates = light.getSkyUpdates();
        skyUpdates.clear();
        skyUpdates.addAll(array);
    }

    private void blockLightArrays(List<byte[]> array) {
        final var light = getLightData();
        if (light == null) {
            LoggerUtility.warn(this, NO_CONTAINS);
            return;
        }

        final List<byte[]> blockUpdates = light.getBlockUpdates();
        blockUpdates.clear();
        blockUpdates.addAll(array);
    }

    boolean containsLightData() {
        return lightDataStructure != null && getLightData() != null;
    }

    private WrappedLevelChunkData.LightData getLightData() {
        if (lightData != null) return lightData;
        if (lightDataStructure.size() < 1) return null;
        lightData = lightDataStructure.read(0);
        return lightData;
    }
}
