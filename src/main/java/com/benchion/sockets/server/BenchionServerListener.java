package com.benchion.sockets.server;

import com.benchion.sockets.packet.PacketContext;
import com.benchion.sockets.server.client.ServerClient;
import io.netty.channel.Channel;

public abstract class BenchionServerListener {
    public void clientConnect(ServerClient client) {}
    public void clientDisconnect(ServerClient client) {}
    public void onPacketReceive(ServerClient client, String message) {}
    public void exceptionCaught(ServerClient client, Throwable throwable) {}
}
