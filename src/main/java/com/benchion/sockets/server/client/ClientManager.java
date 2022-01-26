package com.benchion.sockets.server.client;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;

@Getter
public final class ClientManager {
    private final ArrayList<ServerClient> clients;

    public ClientManager() {
        this.clients = new ArrayList<>();
    }

    public void add(ServerClient... clients) {
        Collections.addAll(this.clients, clients);
    }

    public void remove(ServerClient client) {
        this.clients.remove(client);
        client.disconnect();
    }
}
