package netty.demo.marshalling.server;


import netty.demo.marshalling.entity.SubscribeReq;
import netty.demo.marshalling.entity.SubscribeResp;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class
SubReqServerHandler extends ChannelHandlerAdapter {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("error: " + cause.getMessage());
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SubscribeReq subscribeReq = (SubscribeReq) msg;
        if ("anruoxin".equalsIgnoreCase(subscribeReq.getUserName())) {
            System.out.println("Service accept client subscribe req: " + subscribeReq);
            ctx.writeAndFlush(resp(subscribeReq.getSubReqID()));
        }

    }

    private SubscribeResp resp(int subReqID) {
        SubscribeResp subscribeResp = new SubscribeResp();
        subscribeResp.setSubReqID(subReqID);
        subscribeResp.setRespCode(0);
        subscribeResp.setDesc("netty book order succeed, 3 days later, sent to designated address");
        return subscribeResp;
    }
}
