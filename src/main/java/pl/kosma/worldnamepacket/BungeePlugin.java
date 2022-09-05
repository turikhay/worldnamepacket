package pl.kosma.worldnamepacket;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

public class BungeePlugin extends Plugin implements Listener {
    @Override
    public void onEnable() {
        this.getProxy().registerChannel(WorldNamePacket.CHANNEL_NAME_VOXELMAP);
        this.getProxy().registerChannel(WorldNamePacket.CHANNEL_NAME_XAEROMAP);
        this.getProxy().getPluginManager().registerListener(this, this);
    }

    @Override
    public void onDisable() {
        this.getProxy().unregisterChannel(WorldNamePacket.CHANNEL_NAME_VOXELMAP);
        this.getProxy().unregisterChannel(WorldNamePacket.CHANNEL_NAME_XAEROMAP);
    }

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {
        /*
            Sends world name to Xaero's Minimap/World Map 5 times with 1 second interval,
            because we should wait until it's initialized (but we never know when)
         */
        sendXaeroWorldNameBlindly(event);
    }

    private void sendXaeroWorldNameBlindly(ServerConnectedEvent event) {
        Runnable r = () -> sendWorldName(
                event.getPlayer(),
                event.getServer().getInfo(),
                null,
                WorldNamePacket.CHANNEL_NAME_XAEROMAP
        );
        for (int i = 0; i < 5; i++) {
            getProxy().getScheduler().schedule(this, r, i, TimeUnit.SECONDS);
        }
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (!WorldNamePacket.CHANNEL_NAME_VOXELMAP.equals(event.getTag())) {
            return;
        }
        if (!(event.getSender() instanceof ProxiedPlayer)) {
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        Server server = player.getServer();
        if (server == null) {
            getLogger().warning("Player is not connected to any server, but their map mod requesting a world name");
            return;
        }
        sendWorldName(player, server.getInfo(), event.getData(), WorldNamePacket.CHANNEL_NAME_VOXELMAP);
        event.setCancelled(true);
    }

    private void sendWorldName(ProxiedPlayer player, ServerInfo serverInfo, byte[] request, String tag) {
        String worldName = serverInfo.getName();
        getLogger().info("Sending the world name to the player " + player.getName() + ": " + worldName);
        player.sendData(tag, WorldNamePacket.formatResponsePacket(request, worldName));
    }
}
