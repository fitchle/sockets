package com.benchion.sockets.server;

import com.benchion.sockets.packet.BenchionPacket;
import com.benchion.sockets.packet.PacketContext;
import com.benchion.sockets.packet.PacketRegistry;
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
import lombok.val;

import java.util.Base64;

final class ServerThread {
    private final int port;
    private final BenchionSocketServer server;
    private final PacketRegistry registry;

    private Thread task;

    public ServerThread(BenchionSocketServer server) {
        this.server = server;
        this.port = server.getPort();
        this.registry = server.getRegistry();
    }

    public void run() {
        if (!(this.task == null || this.task.isAlive()))
            return;
        this.task = new Thread(() -> {
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
                                pipeline.addLast(new DelimiterBasedFrameDecoder(server.getBufferLimit(), Delimiters.lineDelimiter()));
                                pipeline.addLast(new StringDecoder(), new StringEncoder());
                                pipeline.addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelActive(ChannelHandlerContext channelHandlerContext) {
                                        PacketSender client = new PacketSender(channelHandlerContext.channel());
                                        server.getListeners().forEach(l -> l.clientConnect(client));
                                        if (channelHandlerContext.channel().isActive()) {
                                            server.getClientManager().add(client);
                                        }
                                    }

                                    @Override
                                    public void channelInactive(ChannelHandlerContext channelHandlerContext) {
                                        PacketSender client = new PacketSender(channelHandlerContext.channel());
                                        server.getListeners().forEach(l -> l.clientDisconnect(client));
                                        if (client != null && server.getClientManager().contains(client)) {
                                            server.getClientManager().remove(client);
                                        }
                                    }

                                    @Override
                                    public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) {
                                        PacketSender client = new PacketSender(channelHandlerContext.channel());
                                        server.getListeners().forEach(l -> l.onPacketReceive(client, new String(Base64.getDecoder().decode((String) o))));

                                        try {
                                            RawPacketResolver resolver = new RawPacketResolver(registry, (String) o);
                                            BenchionPacket packet = resolver.resolve();
                                            PacketContext context = resolver.resolveData();

                                            packet.read(client, context);
                                            packet.handle();
                                        } catch (IllegalPacketFormat | IllegalPacket e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) {
                                        PacketSender client = new PacketSender(channelHandlerContext.channel());
                                        server.getListeners().forEach(l -> l.exceptionCaught(client, throwable));
                                    }
                                });
                                server.getHandlers().forEach(pipeline::addLast);
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
        this.task.start();
    }

    public void shutdown() {
        if (this.task == null || this.task.isAlive())
            return;
        this.task.interrupt();
    }
}
