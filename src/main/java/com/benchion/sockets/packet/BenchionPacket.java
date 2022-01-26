package com.benchion.sockets.packet;

import com.benchion.sockets.server.BenchionServer;
import com.benchion.sockets.server.client.ServerClient;
import io.netty.channel.Channel;

public abstract class BenchionPacket {
    protected final BenchionServer server;

    public BenchionPacket(BenchionServer server) {
        this.server = server;
    }
    public void read(ServerClient client, PacketContext context) {}
    public void handle() {}
    public PacketContext write() {
        return null;
    }
}
