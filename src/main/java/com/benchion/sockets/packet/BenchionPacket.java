package com.benchion.sockets.packet;

/**
 * Packet class for custom packets
 */
public abstract class BenchionPacket {
    /**
     * triggered to read data when the specified packet arrives.
     *
     * @param sender  Packet sender
     * @param context Packet context, that field contains the packet data
     */
    public void read(PacketSender sender, PacketContext context) {
    }

    /**
     * triggered to handle incoming packet.
     */
    public void handle() {
    }

    /**
     * triggered to specify data while sending the specified packet
     *
     * @return Packet context, that field contains the packet data
     */
    public PacketContext write() {
        return null;
    }
}
