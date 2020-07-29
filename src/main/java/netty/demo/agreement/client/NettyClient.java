package netty.demo.agreement.client;

import netty.demo.agreement.NettyConstant;
import netty.demo.agreement.NettyMessageDecoder;
import netty.demo.agreement.NettyMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 利用ChannelPipeline 和ChannelHandler机制，可以非常方便地实现功能解耦和业务产品的定制，通过Handler Chain的机制可以方便的实现切面拦截和定制，相比于
 * AOP的性能更高
 * @author DIDIBABA_CAR_QPW Create in 2020/6/11 15:55
 */
public class NettyClient {

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    NioEventLoopGroup group = new NioEventLoopGroup();

    public static void main(String[] args) {
        try {
            new NettyClient().connect(NettyConstant.REMOTE_IP, NettyConstant.REMOTE_PORT);
        } catch (InterruptedException e) {
            System.out.println("client connect failed: " + e.getMessage());

        }
    }

    public void connect(String host, int port) throws InterruptedException {
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 用于NettyMessage消息解码，为了防止单条消息过大导致内存溢出或者畸形码流解码错位引起内存分配失败，对单条消息长度进行了上线限制
                            ch.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4));
                            // 增加NettyMessage消息编码器
                            ch.pipeline().addLast("MessageEncoder", new NettyMessageEncoder());
                            // 增加读超时处理器
                            ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(50));
                            // 握手请求处理器
                            ch.pipeline().addLast("LoginAuthHandler", new LoginAuthReqHandler());
                            // 心跳消息处理器
                            ch.pipeline().addLast("HeartBeatHandler", new HeartBeatReqHandler());
                        }
                    });
            // 绑定本地端口，用于重复登录保护，从产品角度不允许系统随便使用随机端口
            ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(host, port),
                    new InetSocketAddress(NettyConstant.LOCAL_IP, NettyConstant.LOCAL_PORT)).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            // 所有资源释放完成之后，清空资源，再次发起重连操作
            executor.execute(() -> {
                try {
                    TimeUnit.SECONDS.sleep(5);
                    // 发起重连操作
                    connect(NettyConstant.REMOTE_IP, NettyConstant.REMOTE_PORT);
                } catch (Exception e) {
                    System.out.println("client 重连失败: " + e.getMessage());
                }
            });

        }
    }
}
