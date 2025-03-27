package ru.vladimir.noctyss.event.modules.sounds;

import com.comphenix.protocol.ProtocolManager;
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

public class SoundService implements Module {
    private final JavaPlugin plugin;
    private final PluginManager pluginManager;
    private final ProtocolManager protocolManager;
    private final World world;
    private final EventType eventType;
    private final List<SoundManager> soundManagers;

    private SoundService(Builder builder) {
        this.plugin = builder.getPlugin();
        this.pluginManager = builder.getPluginManager();
        this.protocolManager = builder.getProtocolManager();
        this.world = builder.getWorld();
        this.eventType = builder.getEventType();
        this.soundManagers = builder.getSoundManagers();
    }

    @Override
    public void start() {
        int started = 0;
        for (final SoundManager soundManager : soundManagers) {

            if (soundManager instanceof Controllable) {
                ((Controllable) soundManager).start();
            }

            if (soundManager instanceof Listener) {
                pluginManager.registerEvents((Listener) soundManager, plugin);
            }

            if (soundManager instanceof PacketListener) {
                protocolManager.addPacketListener((PacketListener) soundManager);
            }

            started++;
            LoggerUtility.info(this, "Started '%s' in '%s' for '%s'"
                    .formatted(soundManager.getClass().getSimpleName(), world.getName(), eventType.name()));
        }

        LoggerUtility.info(this, "Started '%d' sound managers in '%s' for '%s'"
                .formatted(started, world.getName(), eventType.name()));
    }

    @Override
    public void stop() {
        int stopped = 0;
        for (final SoundManager soundManager : soundManagers) {

            if (soundManager instanceof Controllable) {
                ((Controllable) soundManager).stop();
            }

            if (soundManager instanceof Listener) {
                HandlerList.unregisterAll((Listener) soundManager);
            }

            if (soundManager instanceof PacketListener) {
                protocolManager.removePacketListener((PacketListener) soundManager);
            }

            stopped++;
            LoggerUtility.info(this, "Stopped '%s' in '%s' for '%s'"
                    .formatted(soundManager.getClass().getSimpleName(), world.getName(), eventType.name()));
        }

        LoggerUtility.info(this, "Stopped '%d' effect managers in '%s' for '%s'"
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
        private final List<SoundManager> soundManagers = new ArrayList<>();

        public Builder addSoundMuter() {
            final SoundMuter soundManager = new SoundMuter(plugin, world);
            soundManagers.add(soundManager);
            return this;
        }

        public SoundService build() {
            return new SoundService(this);
        }
    }
}
