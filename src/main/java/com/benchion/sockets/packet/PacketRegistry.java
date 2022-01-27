package com.benchion.sockets.packet;

import java.util.HashMap;

public final class PacketRegistry {
    private final HashMap<Integer, BenchionPacket> packetMap;

    public PacketRegistry() {
        this.packetMap = new HashMap<>();
    }

    public void register(int id, BenchionPacket packet) {
        this.packetMap.putIfAbsent(id, packet);
    }

    public void unregister(int id) {
        this.packetMap.remove(id);
    }

    public BenchionPacket get(int id) {
        return this.packetMap.get(id);
    }

    public boolean contains(int id) {
        return this.packetMap.containsKey(id);
    }
}
