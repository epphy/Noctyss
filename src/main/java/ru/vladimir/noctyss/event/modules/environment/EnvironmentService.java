package ru.vladimir.noctyss.event.modules.environment;

import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketListener;
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
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.util.ArrayList;
import java.util.List;

public class EnvironmentService implements Module {
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
                pluginManager.registerEvents((Listener) modifier, plugin);
            }

            if (modifier instanceof PacketAdapter) {
                protocolManager.addPacketListener((PacketListener) modifier);
            }

            started++;
            LoggerUtility.info(this, "Added '%s' in '%s' for '%s'"
                    .formatted(modifier.getClass().getSimpleName(), world.getName(), eventType.name()));
        }

        LoggerUtility.info(this, "Added '%d' modifiers in '%s' for '%s'"
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
                HandlerList.unregisterAll((Listener) modifier);
            }

            if (modifier instanceof PacketAdapter) {
                protocolManager.removePacketListener((PacketListener) modifier);
            }

            stopped++;
            LoggerUtility.info(this, "Stopped '%s' in '%s' for '%s'"
                    .formatted(modifier.getClass().getSimpleName(), world.getName(), eventType.name()));
        }

        LoggerUtility.info(this, "Stopped '%d' modifiers in '%s' for '%s'"
                .formatted(stopped, world.getName(), eventType.name()));
    }

    @Getter
    @RequiredArgsConstructor
    public static class Builder {
        private final JavaPlugin plugin;
        private final PluginManager pluginManager;
        private final ProtocolManager protocolManager;
        private final World world;
        private final EventType eventType;
        private final List<EnvironmentModifier> modifiers = new ArrayList<>();

        public Builder addLightingPocketModifier() {
            modifiers.add(new LightingPacketModifier(plugin, world));
            return this;
        }

        public Builder addEntityAIKiller() {
            modifiers.add(new EntityAIKiller(world));
            return this;
        }

        public EnvironmentService build() {
            return new EnvironmentService(this);
        }
    }
}
