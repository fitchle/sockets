package com.benchion.sockets.server;

import com.benchion.sockets.client.BenchionSocketClient;
import com.benchion.sockets.packet.BenchionPacket;
import com.benchion.sockets.packet.PacketID;
import com.benchion.sockets.packet.PacketRegistry;
import com.benchion.sockets.packet.exceptions.IllegalPacket;
import com.benchion.sockets.server.client.ClientManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Server class that builds new TCP Server and return for you.
 *
 * @author Benchion
 * @version 1.0.1
 * @see BenchionSocketClient
 */
@Getter
public final class BenchionSocketServer {
    private final int port;

    private final ArrayList<ChannelHandler> handlers;
    private final ArrayList<EventExecutorGroup> executorGroups;
    private final ArrayList<BenchionSocketServerListener> listeners;
    private final HashMap<ChannelOption, Map.Entry<Object, Boolean>> channelOptionsMap;
    private final ClientManager clientManager;
    private Function<SocketChannel, SocketChannel> socketChannelModify;
    private int bufferLimit;

    private ServerThread serverThread;

    private PacketRegistry registry;
    /**
     * @param port port of server
     */
    public BenchionSocketServer(int port) {
        this.port = port;

        this.handlers = new ArrayList<>();
        this.executorGroups = new ArrayList<>();
        this.listeners = new ArrayList<>();

        this.channelOptionsMap = new HashMap<>();
        this.socketChannelModify = Function.identity();
        this.bufferLimit = 1024;

        this.clientManager = new ClientManager();
        this.registry = new PacketRegistry();
    }

    /**
     * @param handlers Channel handlers
     * @return instance
     */
    public BenchionSocketServer add(ChannelHandler... handlers) {
        Collections.addAll(this.handlers, handlers);
        return this;
    }

    /**
     * @param groups Event Executor Groups
     * @return instance
     */
    public BenchionSocketServer add(EventExecutorGroup... groups) {
        Collections.addAll(this.executorGroups, groups);
        return this;
    }

    /**
     * @param listeners Benchion Server Listeners
     * @return instance
     */
    public BenchionSocketServer add(BenchionSocketServerListener... listeners) {
        Collections.addAll(this.listeners, listeners);
        return this;
    }

    /**
     * @param option  Channel Option
     * @param value   Value for Channel Option
     * @param isChild child status
     * @return instance
     */
    public BenchionSocketServer add(ChannelOption option, Object value, boolean isChild) {
        channelOptionsMap.put(option, new AbstractMap.SimpleEntry<>(value, isChild));
        return this;
    }

    /**
     * @param modifier The server modifier
     * @return instance
     */
    public BenchionSocketServer modify(Function<SocketChannel, SocketChannel> modifier) {
        this.socketChannelModify = modifier;
        return this;
    }

    /**
     * @param bufferLimit buffer size limit in a packet
     * @return instance
     */
    public BenchionSocketServer setBufferLimit(int bufferLimit) {
        this.bufferLimit = bufferLimit;
        return this;
    }

    /**
     * Registers specified packet
     *
     * @param packet the packet to register
     * @throws IllegalPacket
     */
    public BenchionSocketServer register(BenchionPacket packet) throws IllegalPacket {
        if (!packet.getClass().isAnnotationPresent(PacketID.class))
            throw new IllegalPacket("The packet id is not specified in Packet class. Please specify a unique packet id to your packet.");
        PacketID packetID = packet.getClass().getAnnotation(PacketID.class);

        if (registry.contains(packetID.value())) return this;
        registry.register(packetID.value(), packet);
        return this;
    }

    /**
     * That function builds the server
     *
     * @return instance
     */
    public BenchionSocketServer build() {
        this.serverThread = new ServerThread(this);
        return this;
    }


    /**
     * That function send packets to all connected clients
     *
     * @return instance
     */
    public void sendAll(BenchionPacket packet) {
        CompletableFuture<Void> task = CompletableFuture.runAsync(() -> clientManager.getClients().forEach(c -> c.sendPacket(packet)));
        task.join();
    }

    /**
     * Runs the server
     */
    public void run() {
        this.serverThread.run();
    }

    /**
     * Shutdowns the server
     */
    public void shutdown() {
        this.serverThread.shutdown();
    }
}
