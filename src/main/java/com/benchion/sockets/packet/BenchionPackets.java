package com.benchion.sockets.packet;

import com.benchion.sockets.packet.exceptions.IllegalPacket;

import java.util.HashMap;

public final class BenchionPackets {
    private static final HashMap<Integer, BenchionPacket> packetMap = new HashMap<>();

    public static void register(BenchionPacket packet) throws IllegalPacket {
        if (!packet.getClass().isAnnotationPresent(PacketID.class)) throw new IllegalPacket("The packet id is not specified in Packet class. Please specify a unique packet id to your packet.");
        PacketID packetID = packet.getClass().getAnnotation(PacketID.class);

        if (contains(packetID.value())) return;
        packetMap.put(packetID.value(), packet);
    }

    public static void unregister(int id) {
        if (!contains(id)) return;
        packetMap.remove(id);
    }

    public static BenchionPacket get(int id) {
        return packetMap.get(id);
    }

    public static boolean contains(int id) {
        return packetMap.containsKey(id);
    }
}
