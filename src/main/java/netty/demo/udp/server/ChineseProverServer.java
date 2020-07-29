package netty.demo.udp.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

/**
 * @author DIDIBABA_CAR_QPW Create in 2020/6/9 19:42
 */
public class ChineseProverServer {

    private static final int PORT =9999;

    public static void main(String[] args) {
        try {
            new ChineseProverServer().run(PORT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run(int port) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    // 使用UDP通信
                    .channel(NioDatagramChannel.class)
                    // 设置socket参数支持广播
                    .option(ChannelOption.SO_BROADCAST, true)
                    // 不存在客户端和服务端实际连接，不需要为连接（ChannelPipeline）设置handler
                    .handler(new ChineseProverServerHandler());
            bootstrap.bind(port).sync().channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
