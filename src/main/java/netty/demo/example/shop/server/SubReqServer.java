package netty.demo.example.shop.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @author DIDIBABA_CAR_QPW Create in 2020/6/8 16:29
 */
public class SubReqServer {

    private static final int PORT = 9999;

    public static void main(String[] args) {
        try {
            new SubReqServer().bind(PORT);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
                    // 绑定I/O事件处理类（记录日志， 对消息进行编码）
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // 对实现serializable接口的对象进行解码， 使用线程安全的WeakReferenceMap对类加载器进行缓存
                            socketChannel.pipeline().addLast(new ObjectDecoder(1024 * 1024,
                                    ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
                            socketChannel.pipeline().addLast(new ObjectEncoder());
                            socketChannel.pipeline().addLast(new SubReqServerHandler());
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
