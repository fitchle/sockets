package com.benchion.sockets.server;

import com.benchion.sockets.packet.BenchionPacket;
import com.benchion.sockets.packet.PacketContext;
import com.benchion.sockets.packet.PacketSender;
import com.benchion.sockets.packet.exceptions.IllegalPacket;
import com.benchion.sockets.resolver.RawPacketResolver;
import com.benchion.sockets.resolver.exceptions.IllegalPacketFormat;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Base64;
import java.util.concurrent.CompletableFuture;

final class ServerThread {
    private final int port;
    private final BenchionServer server;

    private CompletableFuture<Void> task;

    public ServerThread(BenchionServer server) {
        this.server = server;
        this.port = server.getPort();
    }

    public void run() {
        if (!(this.task == null || this.task.isCancelled() || this.task.isDone() || this.task.isCompletedExceptionally()))
            return;
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
                                ch = server.getSocketChannelModify().apply(ch);
                                ChannelPipeline pipeline = ch.pipeline();
                                server.getExecutorGroups().forEach(pipeline::addLast);
                                server.getHandlers().forEach(pipeline::addLast);
                                pipeline.addLast(new DelimiterBasedFrameDecoder(server.getBufferLimit(), Delimiters.lineDelimiter()));
                                pipeline.addLast(new StringDecoder(), new StringEncoder());
                                server.getListeners().forEach(listener -> pipeline.addLast(new ChannelInboundHandlerAdapter() {
                                    private PacketSender sender;

                                    @Override
                                    public void channelActive(ChannelHandlerContext channelHandlerContext) {
                                        listener.clientConnect(new PacketSender(channelHandlerContext.channel()));
                                        if (channelHandlerContext.channel().isActive()) {
                                            this.sender = new PacketSender(channelHandlerContext.channel());
                                            server.getClientManager().add(sender);
                                        }
                                    }

                                    @Override
                                    public void channelInactive(ChannelHandlerContext channelHandlerContext) {
                                        listener.clientDisconnect(new PacketSender(channelHandlerContext.channel()));
                                        if (sender != null && server.getClientManager().contains(sender)) {
                                            server.getClientManager().remove(sender);
                                        }
                                    }

                                    @Override
                                    public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) throws IllegalPacket, IllegalPacketFormat {
                                        PacketSender client = new PacketSender(channelHandlerContext.channel());
                                        listener.onPacketReceive(client, new String(Base64.getDecoder().decode((String) o)));

                                        RawPacketResolver resolver = new RawPacketResolver((String) o);
                                        BenchionPacket packet = resolver.resolve();
                                        PacketContext context = resolver.resolveData();

                                        packet.read(client, context);
                                        packet.handle();
                                    }

                                    @Override
                                    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) {
                                        listener.exceptionCaught(new PacketSender(channelHandlerContext.channel()), throwable);
                                    }
                                }));
                            }
                        });
                server.getChannelOptionsMap().forEach((opt, val) -> {
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
        if (this.task == null || this.task.isCancelled() || this.task.isDone() || this.task.isCompletedExceptionally())
            return;
        this.task.cancel(true);
    }
}
