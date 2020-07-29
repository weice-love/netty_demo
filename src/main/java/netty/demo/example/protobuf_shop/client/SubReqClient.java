package netty.demo.example.protobuf_shop.client;

import netty.demo.example.protobuf_shop.entity.SubscribeRespProto.SubscribeResp;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

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
                            // 用于半包处理
                            socketChannel.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                            // 解码器， 解码的目标
                            socketChannel.pipeline().addLast(new ProtobufDecoder(SubscribeResp.getDefaultInstance()));
                            socketChannel.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                            socketChannel.pipeline().addLast(new ProtobufEncoder());
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
