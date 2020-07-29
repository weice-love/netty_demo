package netty.demo.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 对于网络事件进行编写操作
 *
 * @author DIDIBABA_CAR_QPW Create in 2020/6/8 10:55
 */
public class TimeServerHandler extends ChannelHandlerAdapter {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 释放相关资源
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 类型转换
        ByteBuf byteBuf = (ByteBuf) msg;
        // 获取缓冲区可读的字节数
        byte[] bytes = new byte[byteBuf.readableBytes()];
        // 将缓冲区的字节数复制到bytes字节数组中
        byteBuf.readBytes(bytes);
        String body = new String(bytes, StandardCharsets.UTF_8);
        System.out.println("The time server receive body: " + body);
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new Date()) : "BAD ORDER";
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.write(resp);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 将消息发送队列中的消息写入到SocketChannel发送个客户端（为了防止频繁唤醒selector进行消息发送，netty的write方法
        // 并不直接将消息写入SocketChannel中，调用write方法只是把待发送的消息方法发送缓冲数组中，再通过调用flush方法，将发送缓冲区的消息
        // 全部写入SocketChannel中）
        ctx.flush();
    }
}
