package netty.demo.udp.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author DIDIBABA_CAR_QPW Create in 2020/6/9 19:45
 */
public class ChineseProverServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static final String[] DICTIONARY = {"只要功夫深，铁棒磨成针。", "旧时王谢堂前燕， 飞入寻常百姓家。"
        , "洛阳亲友如相问， 一片冰心在玉壶。", "一寸光阴一寸金， 寸金难买寸光阴。" , "老骥伏枥， 志在千里。烈士暮年，壮心不已。"};

    private String nextQuote() {
        int quoteId = ThreadLocalRandom.current().nextInt(DICTIONARY.length);
        return DICTIONARY[quoteId];
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket packet)
            throws Exception {
        // 将packet 转成字符串
        String req = packet.content().toString(CharsetUtil.UTF_8);
        System.out.println(req);
        if ("谚语字典查询?".equals(req)) {
            // 第一个参数需要发送的内容，第二个参数目的地址
            ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("谚语查询结果: " + nextQuote(),
                    CharsetUtil.UTF_8), packet.sender()));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("error: " + cause.getMessage());
        ctx.close();
    }
}
