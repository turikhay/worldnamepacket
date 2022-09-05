package pl.kosma.worldnamepacket;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeePlugin extends Plugin implements Listener {
    @Override
    public void onEnable() {
        this.getProxy().registerChannel(WorldNamePacket.CHANNEL_NAME_VOXELMAP);
        this.getProxy().registerChannel(WorldNamePacket.CHANNEL_NAME_XAEROMAP);
    }

    @Override
    public void onDisable() {
        this.getProxy().unregisterChannel(WorldNamePacket.CHANNEL_NAME_VOXELMAP);
        this.getProxy().unregisterChannel(WorldNamePacket.CHANNEL_NAME_XAEROMAP);
    }
}
