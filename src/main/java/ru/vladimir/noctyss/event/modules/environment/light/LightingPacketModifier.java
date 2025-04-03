package ru.vladimir.noctyss.event.modules.environment.light;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vladimir.noctyss.event.Controllable;
import ru.vladimir.noctyss.event.modules.environment.EnvironmentModifier;
import ru.vladimir.noctyss.utility.TaskUtil;

public final class LightingPacketModifier extends PacketAdapter implements EnvironmentModifier, Listener, Controllable {
    private static final PacketType[] LIGHT_PACKET_TYPES = new PacketType[]
            {PacketType.Play.Server.LIGHT_UPDATE, PacketType.Play.Server.MAP_CHUNK};
    private static final long DELAY = 10L;
    private static final byte LIGHT_LEVEL = 0x01;
    private final JavaPlugin pluginInstance;
    private final World world;

    public LightingPacketModifier(JavaPlugin pluginInstance, World world) {
        super(pluginInstance, ListenerPriority.HIGH, LIGHT_PACKET_TYPES);
        this.pluginInstance = pluginInstance;
        this.world = world;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (isWrongWorld(event)) return;
        
        final var packet = event.getPacket();
        final var proxy = new LightUpdateWrapperProxy(packet.getLightUpdateData());
        if (!proxy.containsLightData()) return;

        proxy.resetLight();
        proxy.fillMasks();
        proxy.clearEmptyMasks();
        proxy.setLightLevel(LIGHT_LEVEL);
    }

    private boolean isWrongWorld(PacketEvent event) {
        return !event.getPlayer().getWorld().equals(world);
    }

    /*

    We need to refresh loaded chunks, so the light data would update in them, too.

     */

    @Override
    public void start() {
        refreshChunks();
    }

    @Override
    public void stop() {
        refreshChunks();
    }

    /*

    To partly fix the light glitch, we have a listener which refreshes a new
    chunk a player moves into. So, if there is a light glitch, it gets fixed.

     */

    @EventHandler
    private void on(PlayerMoveEvent event) {
        if (!event.getPlayer().getWorld().equals(world)) return;
        if (!event.hasChangedBlock()) return;

        final Chunk fromChunk = event.getFrom().getBlock().getChunk();
        final Chunk toChunk = event.getTo().getBlock().getChunk();
        if (fromChunk.equals(toChunk)) return;

        refreshChunk(toChunk);
    }

    private void refreshChunks() {
        TaskUtil.getInstance().runDelayedTask(pluginInstance, ()  -> {
            for (final Player player : world.getPlayers()) {
                for (final Chunk chunk : player.getSentChunks()) {
                    refreshChunk(chunk);
                }
            }
        }, DELAY);
    }

    private void refreshChunk(Chunk chunk) {
        TaskUtil.getInstance().runTask(pluginInstance, () -> world.refreshChunk(chunk.getX(), chunk.getZ()));
    }
}
