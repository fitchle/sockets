package com.benchion.sockets.server;

import com.benchion.sockets.packet.PacketSender;

public abstract class BenchionServerListener {
    /**
     * triggered when a client successfully connects to the server
     *
     * @param client client
     */
    public void clientConnect(PacketSender client) {
    }

    /**
     * triggered when a client disconnects from the server
     * @param client client
     */
    public void clientDisconnect(PacketSender client) {
    }

    /**
     * triggered when a packet is received from the client
     *
     * @param client client
     * @param message client's message
     */
    public void onPacketReceive(PacketSender client, String message) {
    }

    /**
     * triggered when an error occurs.
     *
     * @param client client
     * @param throwable throwable
     */
    public void exceptionCaught(PacketSender client, Throwable throwable) {
    }
}
