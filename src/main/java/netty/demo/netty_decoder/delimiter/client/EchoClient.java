package netty.demo.netty_decoder.delimiter.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @author DIDIBABA_CAR_QPW Create in 2020/6/8 11:11
 */
public class EchoClient {

    private static final int PORT = 9999;

    private static final String HOST = "127.0.0.1";

    public static void main(String[] args) {
        try {
            new EchoClient().connect(HOST, PORT);
        } catch (InterruptedException e) {
            System.out.println("client error: " + e.getMessage());
        }
    }

    public void connect(String host, int port) throws InterruptedException {
        // 配置客户端NIO线程
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            // 创建客户端辅助启动类
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    // 配置TCP参数
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
                            //
                            socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
                            //
                            socketChannel.pipeline().addLast(new StringDecoder());
                            // 处理网络I/O事件
                            socketChannel.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            // 发起异步连接操作
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            // 等待客户端链路关闭
            channelFuture.channel().closeFuture().sync();
        } finally {
            // 优雅退出，释放NIO线程
            group.shutdownGracefully();
        }

    }

}
