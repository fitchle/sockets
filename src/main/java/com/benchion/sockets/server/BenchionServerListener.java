package com.benchion.sockets.server;

import com.benchion.sockets.server.client.ServerClient;

public abstract class BenchionServerListener {
    public void clientConnect(ServerClient client) {}
    public void clientDisconnect(ServerClient client) {}
    public void onPacketReceive(ServerClient client, String message) {}
    public void exceptionCaught(ServerClient client, Throwable throwable) {}
}
