package ru.vladimir.noctyss.event.modules.environment;

import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketListener;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.event.Controllable;
import ru.vladimir.noctyss.event.EventType;
import ru.vladimir.noctyss.event.modules.Module;
import ru.vladimir.noctyss.event.modules.environment.light.LightingPacketModifier;
import ru.vladimir.noctyss.utility.LoggerUtility;
import ru.vladimir.noctyss.utility.TaskUtil;

import java.util.ArrayList;
import java.util.List;

public final class EnvironmentService implements Module {
    private final JavaPlugin plugin;
    private final PluginManager pluginManager;
    private final ProtocolManager protocolManager;
    private final World world;
    private final EventType eventType;
    private final List<EnvironmentModifier> modifiers;

    private EnvironmentService(Builder builder) {
        this.plugin = builder.getPlugin();
        this.pluginManager = builder.getPluginManager();
        this.protocolManager = builder.getProtocolManager();
        this.world = builder.getWorld();
        this.eventType = builder.getEventType();
        this.modifiers = builder.getModifiers();
    }

    @Override
    public void start() {
        int started = 0;
        for (final EnvironmentModifier modifier : modifiers) {

            if (modifier instanceof Controllable) {
                ((Controllable) modifier).start();
            }

            if (modifier instanceof Listener) {
                TaskUtil.getInstance().runTask(plugin, () ->
                        pluginManager.registerEvents((Listener) modifier, plugin));
            }

            if (modifier instanceof PacketAdapter) {
                TaskUtil.getInstance().runTask(plugin, () ->
                        protocolManager.addPacketListener((PacketListener) modifier));
            }

            started++;
            LoggerUtility.info(this, "Started '%s' in '%s' for '%s'"
                    .formatted(modifier.getClass().getSimpleName(), world.getName(), eventType.name()));
        }

        LoggerUtility.info(this, "Started all '%d' in '%s' for '%s'"
                .formatted(started, world.getName(), eventType.name()));
    }

    @Override
    public void stop() {
        int stopped = 0;
        for (final EnvironmentModifier modifier : modifiers) {

            if (modifier instanceof Controllable) {
                ((Controllable) modifier).stop();
            }

            if (modifier instanceof Listener) {
                TaskUtil.getInstance().runTask(plugin, () ->
                        HandlerList.unregisterAll((Listener) modifier));
            }

            if (modifier instanceof PacketAdapter) {
                TaskUtil.getInstance().runTask(plugin, () ->
                        protocolManager.removePacketListener((PacketListener) modifier));
            }

            stopped++;
            LoggerUtility.info(this, "Stopped '%s' in '%s' for '%s'"
                    .formatted(modifier.getClass().getSimpleName(), world.getName(), eventType.name()));
        }

        LoggerUtility.info(this, "Stopped all '%d' in '%s' for '%s'"
                .formatted(stopped, world.getName(), eventType.name()));
    }

    @Getter(AccessLevel.PRIVATE)
    @RequiredArgsConstructor
    public static class Builder {
        private final JavaPlugin plugin;
        private final PluginManager pluginManager;
        private final ProtocolManager protocolManager;
        private final World world;
        private final EventType eventType;
        private final List<EnvironmentModifier> modifiers = new ArrayList<>();

        public Builder addLightingPacketModifier(byte lightLevel) {
            modifiers.add(new LightingPacketModifier(plugin, world, lightLevel));
            return this;
        }

        public Builder addEntityAIKiller() {
            modifiers.add(new EntityAIKiller(plugin, world));
            return this;
        }

        public EnvironmentService build() {
            return new EnvironmentService(this);
        }
    }
}
