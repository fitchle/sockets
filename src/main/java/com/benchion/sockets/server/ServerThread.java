package com.benchion.sockets.server;

import com.benchion.sockets.packet.BenchionPacket;
import com.benchion.sockets.packet.PacketContext;
import com.benchion.sockets.packet.exceptions.IllegalPacket;
import com.benchion.sockets.resolver.RawPacketResolver;
import com.benchion.sockets.resolver.exceptions.IllegalPacketFormat;
import com.benchion.sockets.server.client.ServerClient;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

final class ServerThread {
    private final BenchionServer server;
    private final int port;

    private final List<ChannelHandler> handlers;
    private final List<EventExecutorGroup> executorGroups;
    private final ArrayList<BenchionServerListener> listeners;

    private final Function<SocketChannel, SocketChannel> socketChannelModify;
    private final HashMap<ChannelOption, Map.Entry<Object, Boolean>> channelOptionsMap;


    private CompletableFuture<Void> task;

    public ServerThread(BenchionServer server) {
        this.server = server;
        this.port = server.getPort();
        this.handlers = server.getHandlers();
        this.executorGroups = server.getExecutorGroups();
        this.listeners = server.getListeners();

        this.socketChannelModify = server.getSocketChannelModify();
        this.channelOptionsMap = server.getChannelOptionsMap();
    }

    public void run() {
        if (!(this.task == null || this.task.isCancelled() || this.task.isDone() || this.task.isCompletedExceptionally())) return;
        this.task = CompletableFuture.runAsync(() -> {
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) {
                                ch = socketChannelModify.apply(ch);
                                ChannelPipeline pipeline = ch.pipeline();
                                executorGroups.forEach(group -> pipeline.addLast(group));
                                handlers.forEach(handler -> pipeline.addLast(handler));
                                pipeline.addLast(new StringDecoder(), new StringEncoder());
                                listeners.forEach(listener -> pipeline.addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelActive(ChannelHandlerContext channelHandlerContext) {
                                        listener.clientConnect(new ServerClient(channelHandlerContext.channel()));
                                    }

                                    @Override
                                    public void channelInactive(ChannelHandlerContext channelHandlerContext) {
                                        listener.clientDisconnect(new ServerClient(channelHandlerContext.channel()));
                                    }

                                    @Override
                                    public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) throws IllegalPacket, IllegalPacketFormat {
                                        ServerClient client = new ServerClient(channelHandlerContext.channel());
                                        listener.onPacketReceive(client, new String(Base64.getDecoder().decode((String) o)));

                                        RawPacketResolver resolver = new RawPacketResolver((String) o);
                                        BenchionPacket packet = resolver.resolve();
                                        PacketContext context = resolver.resolveData();

                                        packet.read(client, context);
                                        packet.handle();
                                    }

                                    @Override
                                    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) {
                                        listener.exceptionCaught(new ServerClient(channelHandlerContext.channel()), throwable);
                                    }
                                }));
                            }
                        });
                this.channelOptionsMap.forEach((opt, val) -> {
                    if (val.getValue()) {
                        b.childOption(opt, val.getKey());
                        return;
                    }
                    b.option(opt, val.getKey());
                });
                ChannelFuture f = b.bind(port).sync();
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        });

        this.task.join();
    }

    public void shutdown() {
        if (this.task == null || this.task.isCancelled() || this.task.isDone() || this.task.isCompletedExceptionally()) return;
        this.task.cancel(true);
    }
}
