package com.benchion.sockets.server.client;

import com.benchion.sockets.packet.PacketSender;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * That class manages clients
 */
@Getter
public final class ClientManager {
    private final ArrayList<PacketSender> clients;

    public ClientManager() {
        this.clients = new ArrayList<>();
    }

    /**
     * Adds client to client list
     *
     * @param clients clients to be registered
     */
    public void add(PacketSender... clients) {
        Collections.addAll(this.clients, clients);
    }

    /**
     * Removes the client from client list
     *
     * @param client client to remove from list
     */
    public void remove(PacketSender client) {
        this.clients.remove(client);
    }

    /**
     * @param client client to check
     * @return if contains in client list
     */
    public boolean contains(PacketSender client) {
        return this.clients.contains(client);
    }
}
