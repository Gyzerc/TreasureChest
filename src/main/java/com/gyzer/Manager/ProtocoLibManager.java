package com.gyzer.Manager;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;

public class ProtocoLibManager {
    private final ProtocolManager protocolManager;

    public ProtocoLibManager() {
        protocolManager = ProtocolLibrary.getProtocolManager();;
    }

    public void sendPacket(Player player, PacketContainer... packets) {
        if (!player.isOnline()) {return;}
        for (PacketContainer packet : packets) {
            send(player, packet);
        }
    }

    public void send(Player player, PacketContainer packet) {
        this.protocolManager.sendServerPacket(player, packet);
    }
}
