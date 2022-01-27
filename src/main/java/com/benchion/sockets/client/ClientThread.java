package com.benchion.sockets.client;

import com.benchion.sockets.packet.BenchionPacket;
import com.benchion.sockets.packet.PacketContext;
import com.benchion.sockets.packet.PacketRegistry;
import com.benchion.sockets.packet.PacketSender;
import com.benchion.sockets.packet.exceptions.IllegalPacket;
import com.benchion.sockets.resolver.RawPacketResolver;
import com.benchion.sockets.resolver.exceptions.IllegalPacketFormat;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

final class ClientThread {
    private final String host;
    private final int port;
    private final BenchionSocketClient client;
    private final PacketRegistry registry;

    private Thread task;

    public ClientThread(BenchionSocketClient client) {
        this.client = client;
        this.host = client.getHost();
        this.port = client.getPort();
        this.registry = client.getPacketRegistry();
    }

    public void run() {
        if (!(this.task == null || this.task.isInterrupted()))
            return;
        this.task = new Thread(() -> {
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
                        pipeline.addLast(new DelimiterBasedFrameDecoder(client.getBufferLimit(), Delimiters.lineDelimiter()));
                        pipeline.addLast(new StringDecoder(), new StringEncoder());
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelActive(ChannelHandlerContext channelHandlerContext) {
                                PacketSender server = new PacketSender(channelHandlerContext.channel());
                                client.getListeners().forEach(l -> l.onConnect(server));
                            }

                            @Override
                            public void channelInactive(ChannelHandlerContext channelHandlerContext) {
                                PacketSender server = new PacketSender(channelHandlerContext.channel());
                                client.getListeners().forEach(l -> l.onDisconnect(server));
                            }

                            @Override
                            public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) throws IllegalPacketFormat, IllegalPacket {
                                PacketSender server = new PacketSender(channelHandlerContext.channel());
                                client.getListeners().forEach(l -> l.onPacketReceive(server, new String(Base64.getDecoder().decode((String) o))));

                                RawPacketResolver resolver = new RawPacketResolver(registry, (String) o);
                                BenchionPacket packet = resolver.resolve();
                                PacketContext context = resolver.resolveData();

                                packet.read(server, context);
                                packet.handle();
                            }
                        });

                        client.getHandlers().forEach(pipeline::addLast);
                    }
                });
                client.getChannelOptionsMap().forEach(b::option);
                ChannelFuture f = b.connect(host, port).sync();
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
            }
        });

        this.task.start();
    }

    public void shutdown() {
        if (this.task == null || this.task.isInterrupted())
            return;
        this.task.interrupt();
    }
}
