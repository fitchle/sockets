import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class TestClient {
    public static void main(String[] args) {
        int port = 8888;
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(new StringDecoder(), new StringEncoder());
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {

                        @Override
                        public void channelActive(ChannelHandlerContext channelHandlerContext) {
                            System.out.println("bağlandı");
                            channelHandlerContext.writeAndFlush(Unpooled.copiedBuffer(Base64.getEncoder().encode("{}".getBytes(StandardCharsets.UTF_8))));
                        }

                        @Override
                        public void channelInactive(ChannelHandlerContext channelHandlerContext) {
                            System.out.println("çıktı");
                        }

                        @Override
                        public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) {
                            String msg = (String) o;
                            System.out.println(msg);
                        }

                    });
                }
            });

            // Start the client.
            ChannelFuture f = b.connect("localhost", port).sync(); // (5)

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
