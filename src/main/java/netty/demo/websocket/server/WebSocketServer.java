package netty.demo.websocket.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author DIDIBABA_CAR_QPW Create in 2020/6/9 16:00
 */
public class WebSocketServer {

    private static final int PORT = 9999;

    public static void main(String[] args) {
        try {
            new WebSocketServer().run(PORT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run(int port) throws InterruptedException {
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
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // 将请求和应答消息编码或解码为HTTP消息
                            socketChannel.pipeline().addLast("http-codec", new HttpServerCodec());
                            // 将多个消息转换位单一的fullHttpRequest 或者fullHttpResponse
                            socketChannel.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
                            // 支持异步发送大的码流（大文件传输），但不占用过多的内存，防止发生java内存溢出错误
                            // 来向客户端发送HTML5文件，主要用于支持浏览器和服务断进行websocket通信
                            socketChannel.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                            socketChannel.pipeline().addLast("handler", new WebSocketServerHandler());
                        }
                    });

            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            Channel channel = channelFuture.channel();
            System.out.println("Web socket server started at port: " + port);
            System.out.println("Open your browser and navigate to http://localhost:" + port + "/");
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
