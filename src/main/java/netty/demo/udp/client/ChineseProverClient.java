package netty.demo.udp.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import java.net.InetSocketAddress;

/**
 * @author DIDIBABA_CAR_QPW Create in 2020/6/9 19:42
 */
public class ChineseProverClient {

    private static final int PORT =9999;

    public static void main(String[] args) {
        try {
            new ChineseProverClient().run(PORT);
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
                    .handler(new ChineseProverClientHandler());
            Channel channel = bootstrap.bind(0).sync().channel();
            // 向网段内的所有机器广播UDP消息
            channel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("谚语字典查询?", CharsetUtil.UTF_8),
                    new InetSocketAddress("255.255.255.255", port))).sync();
            if (!channel.closeFuture().await(15000)) {
                System.out.println("查询超时！");
            }
        } finally {
            group.shutdownGracefully();
        }
    }
}
