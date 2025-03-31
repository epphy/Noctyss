package ru.vladimir.noctyss.event.modules.environment.light;

import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedLevelChunkData;
import lombok.RequiredArgsConstructor;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

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

    void setLightLevel(byte level) {
        final byte[] bytes = new byte[2048];
        Arrays.fill(bytes, level);

        final List<byte[]> updates = new ArrayList<>();
        for (int i = 0; i < 18; i++) {
            updates.add(bytes);
        }

        blockLightArrays(updates);
        skyLightArrays(updates);
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
