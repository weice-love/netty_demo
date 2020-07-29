package netty.demo.agreement.server;

import netty.demo.agreement.NettyConstant;
import netty.demo.agreement.NettyMessageDecoder;
import netty.demo.agreement.NettyMessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * @author DIDIBABA_CAR_QPW Crebate in 2020/6/11 16:16
 */
public class NettyServer {

    public static void main(String[] args) {
        try {
            new NettyServer().bind();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void bind() throws InterruptedException {
        // 配置服务端的NIO线程
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 用于NettyMessage消息解码，为了防止单条消息过大导致内存溢出或者畸形码流解码错位引起内存分配失败，对单条消息长度进行了上线限制
                            ch.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4));
                            // 增加NettyMessage消息编码器
                            ch.pipeline().addLast(new NettyMessageEncoder());
                            // 增加读超时处理器
                            ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(50));
                            // 握手请求处理器
                            ch.pipeline().addLast(new LoginAuthRespHandler());
                            // 心跳消息处理器
                            ch.pipeline().addLast(new HeartBeatRespHandler());
                        }
                    });
            // 绑定端口， 同步等待成功
            ChannelFuture channelFuture = serverBootstrap.bind(NettyConstant.REMOTE_IP, NettyConstant.REMOTE_PORT)
                    .sync();
            System.out.println("Netty server start ok : " + NettyConstant.REMOTE_IP + ":" + NettyConstant.REMOTE_PORT);
//            channelFuture.channel().closeFuture().sync();
        } finally {

        }
    }

}
