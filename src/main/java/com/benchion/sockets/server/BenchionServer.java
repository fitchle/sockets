package com.benchion.sockets.server;

import com.benchion.sockets.packet.BenchionPacket;
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
 * @version 1.0
 * @see com.benchion.sockets.client.BenchionClient
 */
@Getter
public final class BenchionServer {
    private final int port;
    private final ArrayList<ChannelHandler> handlers;
    private final ArrayList<EventExecutorGroup> executorGroups;
    private final ArrayList<BenchionServerListener> listeners;
    private final HashMap<ChannelOption, Map.Entry<Object, Boolean>> channelOptionsMap;
    private final ClientManager clientManager;
    private Function<SocketChannel, SocketChannel> socketChannelModify;

    private ServerThread serverThread;


    /**
     * @param port port of server
     */
    public BenchionServer(int port) {
        this.port = port;

        this.handlers = new ArrayList<>();
        this.executorGroups = new ArrayList<>();
        this.listeners = new ArrayList<>();

        this.channelOptionsMap = new HashMap<>();
        this.socketChannelModify = Function.identity();

        this.clientManager = new ClientManager();
    }

    /**
     * @param handlers Channel handlers
     * @return instance
     */
    public BenchionServer add(ChannelHandler... handlers) {
        Collections.addAll(this.handlers, handlers);
        return this;
    }

    /**
     * @param groups Event Executor Groups
     * @return instance
     */
    public BenchionServer add(EventExecutorGroup... groups) {
        Collections.addAll(this.executorGroups, groups);
        return this;
    }

    /**
     * @param listeners Benchion Server Listeners
     * @return instance
     */
    public BenchionServer add(BenchionServerListener... listeners) {
        Collections.addAll(this.listeners, listeners);
        return this;
    }

    /**
     * @param option Channel Option
     * @param value  Value for Channel Option
     * @param isChild child status
     * @return instance
     */
    public BenchionServer add(ChannelOption option, Object value, boolean isChild) {
        channelOptionsMap.put(option, new AbstractMap.SimpleEntry<>(value, isChild));
        return this;
    }

    /**
     * @param modifier The server modifier
     * @return instance
     */
    public BenchionServer modify(Function<SocketChannel, SocketChannel> modifier) {
        this.socketChannelModify = modifier;
        return this;
    }

    /**
     * That function builds the server
     *
     * @return instance
     */
    public BenchionServer build() {
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
