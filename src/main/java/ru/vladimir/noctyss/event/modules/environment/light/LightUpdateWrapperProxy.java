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
public class LightUpdateWrapperProxy {
    private static final String NO_CONTAINS = "Failed to process light: light packet is not found";
    private final StructureModifier<WrappedLevelChunkData.LightData> lightDataStructure;
    private WrappedLevelChunkData.LightData lightData;

    public void fillMasks() {
        var light = getLightData();
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

    public void clearEmptyMasks() {
        var light = getLightData();
        if (light == null) {
            LoggerUtility.warn(this, NO_CONTAINS);
            return;
        }

        light.setEmptyBlockYMask(new BitSet());
        light.setEmptySkyYMask(new BitSet());
    }

    public void setLightLevel(byte level) {
        final byte[] bytes = new byte[2048];
        Arrays.fill(bytes, level);

        final List<byte[]> updates = new ArrayList<>();
        for (int i = 0; i < 18; i++) {
            updates.add(bytes);
        }

        blockLightArrays(updates);
        skyLightArrays(updates);
    }

    private void skyLightArrays(List<byte[]> array) {
        var light = getLightData();
        if (light == null) {
            LoggerUtility.warn(this, NO_CONTAINS);
            return;
        }

        final List<byte[]> skyUpdates = light.getSkyUpdates();
        skyUpdates.clear();
        skyUpdates.addAll(array);
    }

    private void blockLightArrays(List<byte[]> array) {
        var light = getLightData();
        if (light == null) {
            LoggerUtility.warn(this, NO_CONTAINS);
            return;
        }

        final List<byte[]> blockUpdates = light.getBlockUpdates();
        blockUpdates.clear();
        blockUpdates.addAll(array);
    }

    public boolean isContainsLightData() {
        return lightDataStructure != null && getLightData() != null;
    }

    private WrappedLevelChunkData.LightData getLightData() {
        if (lightData != null) return lightData;
        if (lightDataStructure.size() < 1) return null;
        lightData = lightDataStructure.read(0);
        return lightData;
    }
}
