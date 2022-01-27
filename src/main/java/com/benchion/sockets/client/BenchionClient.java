package com.benchion.sockets.client;

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
 * @see com.benchion.sockets.server.BenchionServer
 */
@Getter
public final class BenchionClient {
    private final String host;
    private final int port;

    private final ArrayList<ChannelHandler> handlers;
    private final ArrayList<EventExecutorGroup> executorGroups;
    private final ArrayList<BenchionClientListener> listeners;

    private final HashMap<ChannelOption, Object> channelOptionsMap;
    private Function<SocketChannel, SocketChannel> socketChannelModify;
    private int bufferLimit;

    private ClientThread clientThread;


    /**
     * @param host Server's hostname
     * @param port Server's port
     */
    public BenchionClient(String host, int port) {
        this.host = host;
        this.port = port;

        this.handlers = new ArrayList<>();
        this.executorGroups = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.channelOptionsMap = new HashMap<>();
        this.socketChannelModify = Function.identity();
        this.bufferLimit = 1024;
    }

    /**
     * @param handlers Channel handlers
     * @return instance
     */
    public BenchionClient add(ChannelHandler... handlers) {
        Collections.addAll(this.handlers, handlers);
        return this;
    }

    /**
     * @param groups Event Executor Groups
     * @return instance
     */
    public BenchionClient add(EventExecutorGroup... groups) {
        Collections.addAll(this.executorGroups, groups);
        return this;
    }

    /**
     * @param listeners Benchion Client Listeners
     * @return instance
     */
    public BenchionClient add(BenchionClientListener... listeners) {
        Collections.addAll(this.listeners, listeners);
        return this;
    }


    /**
     * @param option Channel Option
     * @param value  Value for Channel Option
     * @return instance
     */
    public BenchionClient add(ChannelOption option, Object value) {
        channelOptionsMap.put(option, value);
        return this;
    }

    /**
     * @param modifier The client modifier
     * @return instance
     */
    public BenchionClient modify(Function<SocketChannel, SocketChannel> modifier) {
        this.socketChannelModify = modifier;
        return this;
    }

    public BenchionClient setBufferLimit(int bufferLimit) {
        this.bufferLimit = bufferLimit;
        return this;
    }

    /**
     * That function builds the client
     *
     * @return instance
     */
    public BenchionClient build() {
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
