package netty.demo.netty_decoder.fixlength.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author DIDIBABA_CAR_QPW Create in 2020/6/8 10:40
 */
public class EchoServer {

    private static final int PORT = 9999;

    public static void main(String[] args) {
        // 启动服务端
        try {
            new EchoServer().bind(PORT);
        } catch (InterruptedException e) {
            System.out.println("server error: " + e.getMessage());
        }

    }

    public void bind(int port) throws InterruptedException {
        // 配置服务的端的NIO线程组

        // 接收客户端的连接
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        // 进行socketChannel的网络读写
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 启动NIO服务端的辅助启动类， 降低服务端的开发复杂读
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // 配置TCP参数
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    // 配置日志
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 绑定I/O事件处理类（记录日志， 对消息进行编码）
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new FixedLengthFrameDecoder(20));
                            socketChannel.pipeline().addLast(new StringDecoder());
                            socketChannel.pipeline().addLast(new EchoServerHandler());
                        }
                    });
            // 绑定端口, 等待同步成功 （异步通知的回调）
            ChannelFuture f = serverBootstrap.bind(port).sync();

            // 等待服务端口监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            // 优雅退出，释放线程资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
