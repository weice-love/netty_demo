package netty.demo.example.shop.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @author DIDIBABA_CAR_QPW Create in 2020/6/8 16:43
 */
public class SubReqClient {

    private static final int PORT = 9999;

    private static final String HOST = "127.0.0.1";

    public static void main(String[] args) {
        try {
            new SubReqClient().connect(HOST, PORT);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
                            // 禁止类加载器进行缓存，在基于osgi的动态模块话编程中经常使用，由于osgi
                            // 的bundle可以进行热部署和热升级，当某个bundle升级后，他对应的类加载器叶将一起升级，因此在
                            // 动态模块化编程中，很少对类进行缓存，他随时可能变化
                            socketChannel.pipeline().addLast(new ObjectDecoder(1024,
                                    ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
                            socketChannel.pipeline().addLast(new ObjectEncoder());
                            // 处理网络I/O事件
                            socketChannel.pipeline().addLast(new SubReqClientHandler());
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
