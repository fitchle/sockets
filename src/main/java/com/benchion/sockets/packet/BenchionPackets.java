package com.benchion.sockets.packet;

import com.benchion.sockets.packet.exceptions.IllegalPacket;

import java.util.HashMap;

/**
 * That class holds the custom packets
 *
 * @see BenchionPacket
 */
public final class BenchionPackets {
    private static final HashMap<Integer, BenchionPacket> packetMap = new HashMap<>();

    /**
     * Registers specified packet
     *
     * @param packet the packet to register
     * @throws IllegalPacket
     */
    public static void register(BenchionPacket packet) throws IllegalPacket {
        if (!packet.getClass().isAnnotationPresent(PacketID.class))
            throw new IllegalPacket("The packet id is not specified in Packet class. Please specify a unique packet id to your packet.");
        PacketID packetID = packet.getClass().getAnnotation(PacketID.class);

        if (contains(packetID.value())) return;
        packetMap.put(packetID.value(), packet);
    }

    /**
     * Unregisters specified packet
     *
     * @param id Packet ID
     * @see PacketID
     */
    public static void unregister(int id) {
        if (!contains(id)) return;
        packetMap.remove(id);
    }

    /**
     * @param id Packet ID
     * @return the packet with the entered id
     * @see PacketID
     */
    public static BenchionPacket get(int id) {
        return packetMap.get(id);
    }

    /**
     * @param id Packet ID
     * @return whether the packet is registered or not
     */
    public static boolean contains(int id) {
        return packetMap.containsKey(id);
    }
}
