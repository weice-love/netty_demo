package netty.demo.http.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author DIDIBABA_CAR_QPW Create in 2020/6/8 20:26
 */
public class HttpFileServer {

    private static final int PORT = 9999;

    private static final String DEFAULT_URL = "/src/main/java/cao/sb/";

    public static void main(String[] args) {
        try {
            System.out.println(System.getProperty("user.dir"));
            new HttpFileServer().run(PORT, DEFAULT_URL);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run(final int port, final String url) throws InterruptedException {

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
                            // 消息解码器 ,每个消息会生成多个消息对象（httprequest，httpcontent， lasthttpcontent）
                            socketChannel.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                            // 将多个消息转换位单一的fullHttpRequest 或者fullHttpResponse
                            socketChannel.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
                            // 响应解码器
                            socketChannel.pipeline().addLast("http-encoder", new HttpResponseEncoder());
                            // 支持异步发送大的码流（大文件传输），但不占用过多的内存，防止发生java内存溢出错误
                            socketChannel.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                            socketChannel.pipeline().addLast("fileServerHandler", new HttpFileServerHandler(url));
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind("127.0.0.1", port).sync();
            System.out.println("文件目录服务器启动，网址是 ： http://127.0.0.1:" + port + url);
            channelFuture.channel().closeFuture().sync();

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
