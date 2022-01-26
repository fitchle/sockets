package com.benchion.sockets.server.client;

import com.benchion.sockets.packet.BenchionPacket;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@Getter
public final class ServerClient {
    private final Channel channel;

    public void sendPacket(BenchionPacket packet) {
        CompletableFuture<Void> task = CompletableFuture.runAsync(() -> channel.writeAndFlush(Base64.getEncoder().encode(packet.write().toString().getBytes(StandardCharsets.UTF_8))));
        task.join();
    }

    public void disconnect() {
        this.channel.disconnect();
    }
}
