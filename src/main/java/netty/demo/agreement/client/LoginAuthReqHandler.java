package netty.demo.agreement.client;

import netty.demo.agreement.Header;
import netty.demo.agreement.MessageType;
import netty.demo.agreement.NettyMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;


/**
 * 握手认证, 客户端
 * @author DIDIBABA_CAR_QPW Create in 2020/6/11 14:41
 */
public class LoginAuthReqHandler extends ChannelHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(buildLoginReq());
        System.out.println("client: send LoginAuthReq ---> " + buildLoginReq());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage nettyMessage = (NettyMessage) msg;
        // 如果是握手应答消息，需要判断是否认证成功
        if (nettyMessage.getHeader() != null && nettyMessage.getHeader().getType() == MessageType.LOGIN_RESP.getValue()) {
            byte loginResult = (byte) nettyMessage.getBody();
            if (loginResult != (byte) 0) {
                // 握手失败，关闭连接
                ctx.close();
            }
            System.out.println("login is ok : " + nettyMessage);
            ctx.fireChannelRead(msg);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("LoginAuthReqHandler exceptionCaught: " + cause.getMessage());
        ctx.fireExceptionCaught(cause);
    }

    private NettyMessage buildLoginReq() {
        NettyMessage nettyMessage = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_REQ.getValue());
        nettyMessage.setHeader(header);
        nettyMessage.setBody((byte) 0);
        return nettyMessage;
    }
}
