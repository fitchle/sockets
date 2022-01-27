package com.benchion.sockets.packet;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Getter
public final class PacketSender {
    private final Channel channel;

    public PacketSender(Channel channel) {
        this.channel = channel;
    }

    /**
     * Sends a packet to Client/Server
     *
     * @param packet Sent Packet
     */
    public void sendPacket(BenchionPacket packet) {
        Thread t = new Thread(() -> channel.writeAndFlush(Unpooled.copiedBuffer(new String(Base64.getEncoder().encode(packet.write().toString().getBytes(StandardCharsets.UTF_8))) + "\n", StandardCharsets.UTF_8)));
        t.start();
    }

    /**
     * Disconnects from channel
     */
    public void disconnect() {
        channel.disconnect();
    }
}
