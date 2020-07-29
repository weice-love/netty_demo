package netty.demo.netty_zhanbao.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import java.nio.charset.StandardCharsets;

/**
 * @author DIDIBABA_CAR_QPW Create in 2020/6/8 11:19
 */
public class TimeClientHandler extends ChannelHandlerAdapter {

    private int counter;

    private byte[] req;

    public TimeClientHandler() {
        req = ("QUERY TIME ORDER" + System.getProperty("line.separator")).getBytes();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("error: " + cause.getMessage());
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 客户端和服务端TCP链路建立成功之后，NIO调用channelActive方法，发送查询时间的指令给服务端
        // 调用writeAndFlush 发送给服务端
        ByteBuf message;
        for (int i = 0; i < 100; i++) {
            message= Unpooled.buffer(req.length);
            message.writeBytes(req);
            ctx.writeAndFlush(message);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 当服务端返回应答消息，当前方法被调用
        String body = (String)msg;
        System.out.println("client receive body: " + body + ", counter: " + ++counter);
    }
}
