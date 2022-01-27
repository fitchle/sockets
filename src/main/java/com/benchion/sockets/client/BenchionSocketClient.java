package com.benchion.sockets.client;

import com.benchion.sockets.packet.BenchionPacket;
import com.benchion.sockets.packet.PacketID;
import com.benchion.sockets.packet.PacketRegistry;
import com.benchion.sockets.packet.exceptions.IllegalPacket;
import com.benchion.sockets.server.BenchionSocketServer;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.function.Function;

/**
 * Client class that builds new client and return for you.
 *
 * @author Benchion
 * @version 1.0.1
 * @see BenchionSocketServer
 */
@Getter
public final class BenchionSocketClient {
    private final String host;
    private final int port;

    private final ArrayList<ChannelHandler> handlers;
    private final ArrayList<EventExecutorGroup> executorGroups;
    private final ArrayList<BenchionSocketClientListener> listeners;

    private final HashMap<ChannelOption, Object> channelOptionsMap;
    private final PacketRegistry packetRegistry;
    private Function<SocketChannel, SocketChannel> socketChannelModify;
    private int bufferLimit;

    private ClientThread clientThread;


    /**
     * @param host Server's hostname
     * @param port Server's port
     */
    public BenchionSocketClient(String host, int port) {
        this.host = host;
        this.port = port;

        this.handlers = new ArrayList<>();
        this.executorGroups = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.channelOptionsMap = new HashMap<>();
        this.socketChannelModify = Function.identity();
        this.bufferLimit = 1024;
        this.packetRegistry = new PacketRegistry();
    }

    /**
     * @param handlers Channel handlers
     * @return instance
     */
    public BenchionSocketClient add(ChannelHandler... handlers) {
        Collections.addAll(this.handlers, handlers);
        return this;
    }

    /**
     * @param groups Event Executor Groups
     * @return instance
     */
    public BenchionSocketClient add(EventExecutorGroup... groups) {
        Collections.addAll(this.executorGroups, groups);
        return this;
    }

    /**
     * @param listeners Benchion Client Listeners
     * @return instance
     */
    public BenchionSocketClient add(BenchionSocketClientListener... listeners) {
        Collections.addAll(this.listeners, listeners);
        return this;
    }


    /**
     * @param option Channel Option
     * @param value  Value for Channel Option
     * @return instance
     */
    public BenchionSocketClient add(ChannelOption option, Object value) {
        channelOptionsMap.put(option, value);
        return this;
    }

    public BenchionSocketClient register(BenchionPacket... packets) throws IllegalPacket {
        for (BenchionPacket packet : packets) {
            if (!packet.getClass().isAnnotationPresent(PacketID.class))
                throw new IllegalPacket("The packet id is not specified in Packet class. Please specify a unique packet id to your packet.");
            int packetId = packet.getClass().getDeclaredAnnotation(PacketID.class).value();
            packetRegistry.register(packetId, packet);
        }
        return this;
    }

    /**
     * @param modifier The client modifier
     * @return instance
     */
    public BenchionSocketClient modify(Function<SocketChannel, SocketChannel> modifier) {
        this.socketChannelModify = modifier;
        return this;
    }

    public BenchionSocketClient setBufferLimit(int bufferLimit) {
        this.bufferLimit = bufferLimit;
        return this;
    }

    /**
     * That function builds the client
     *
     * @return instance
     */
    public BenchionSocketClient build() {
        this.clientThread = new ClientThread(this);
        return this;
    }

    /**
     * Connect to server
     */
    public void connect() {
        this.clientThread.run();
    }

    /**
     * Shutdowns the client
     */
    public void shutdown() {
        this.clientThread.shutdown();
    }
}
