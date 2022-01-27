package com.benchion.sockets.client;

import com.benchion.sockets.packet.BenchionPacket;
import com.benchion.sockets.packet.PacketContext;
import com.benchion.sockets.packet.PacketSender;
import com.benchion.sockets.packet.exceptions.IllegalPacket;
import com.benchion.sockets.resolver.RawPacketResolver;
import com.benchion.sockets.resolver.exceptions.IllegalPacketFormat;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Base64;
import java.util.concurrent.CompletableFuture;

final class ClientThread {
    private final String host;
    private final int port;
    private final BenchionClient client;

    private CompletableFuture<Void> task;

    public ClientThread(BenchionClient client) {
        this.client = client;
        this.host = client.getHost();
        this.port = client.getPort();
    }

    public void run() {
        if (!(this.task == null || this.task.isCancelled() || this.task.isDone() || this.task.isCompletedExceptionally()))
            return;

        this.task = CompletableFuture.runAsync(() -> {
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                Bootstrap b = new Bootstrap();
                b.group(workerGroup);
                b.channel(NioSocketChannel.class);
                b.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch = client.getSocketChannelModify().apply(ch);
                        ChannelPipeline pipeline = ch.pipeline();
                        client.getExecutorGroups().forEach(pipeline::addLast);
                        client.getHandlers().forEach(pipeline::addLast);
                        pipeline.addLast(new StringDecoder(), new StringEncoder());
                        client.getListeners().forEach(listener -> pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelActive(ChannelHandlerContext channelHandlerContext) {
                                listener.onConnect(new PacketSender(channelHandlerContext.channel()));
                            }

                            @Override
                            public void channelInactive(ChannelHandlerContext channelHandlerContext) {
                                listener.onDisconnect(new PacketSender(channelHandlerContext.channel()));
                            }

                            @Override
                            public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) throws IllegalPacketFormat, IllegalPacket {
                                PacketSender server = new PacketSender(channelHandlerContext.channel());
                                listener.onPacketReceive(server, new String(Base64.getDecoder().decode((String) o)));

                                RawPacketResolver resolver = new RawPacketResolver((String) o);
                                BenchionPacket packet = resolver.resolve();
                                PacketContext context = resolver.resolveData();

                                packet.read(server, context);
                            }
                        }));
                    }
                });
                client.getChannelOptionsMap().forEach((k, v) -> b.option(k, v));
                ChannelFuture f = b.connect(host, port).sync();
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
            }
        });

        this.task.join();
    }

    public void shutdown() {
        if (this.task == null || this.task.isCancelled() || this.task.isDone() || this.task.isCompletedExceptionally())
            return;
        this.task.cancel(true);
    }
}
