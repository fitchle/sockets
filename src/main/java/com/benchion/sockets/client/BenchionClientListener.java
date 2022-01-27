package com.benchion.sockets.client;

import com.benchion.sockets.packet.PacketSender;

/**
 * That  listener adapter for client
 */
public abstract class BenchionClientListener {

    /**
     * triggered when client successfully connects to the server
     *
     * @param server server
     */
    public void onConnect(PacketSender server) {
    }

    /**
     * triggered when client disconnects from the server
     *
     * @param server server
     */
    public void onDisconnect(PacketSender server) {
    }

    /**
     * triggered when a packet is received from the server
     *
     * @param server server
     * @param message server's message
     */
    public void onPacketReceive(PacketSender server, String message) {
    }

    /**
     * triggered when an error occurs.
     *
     * @param server server
     * @param throwable throwable
     */
    public void exceptionCaught(PacketSender server, Throwable throwable) {
    }
}
