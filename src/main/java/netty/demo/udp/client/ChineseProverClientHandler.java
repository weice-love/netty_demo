package netty.demo.udp.client;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author DIDIBABA_CAR_QPW Create in 2020/6/9 19:45
 */
public class ChineseProverClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket packet)
            throws Exception {
        // 将packet 转成字符串
        String response = packet.content().toString(CharsetUtil.UTF_8);
        if (response.startsWith("谚语查询结果: ")) {
            System.out.println(response);
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("error: " + cause.getMessage());
        ctx.close();
    }
}
