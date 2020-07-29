package netty.demo.example.protobuf_shop.server;


import netty.demo.example.protobuf_shop.entity.SubscribeReqProto.SubscribeReq;
import netty.demo.example.protobuf_shop.entity.SubscribeRespProto.SubscribeResp;
import netty.demo.example.protobuf_shop.entity.SubscribeRespProto.SubscribeResp.Builder;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author DIDIBABA_CAR_QPW Create in 2020/6/8 16:35
 */
public class SubReqServerHandler extends ChannelHandlerAdapter {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("error: " + cause.getMessage());
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SubscribeReq subscribeReq = (SubscribeReq) msg;
        if ("anruoxin".equalsIgnoreCase(subscribeReq.getUserName())) {
            System.out.println("Service accept client subscribe req: " + subscribeReq.toString()
                + ", address => " + ((SubscribeReq)msg).getAddress());
            ctx.writeAndFlush(resp(subscribeReq.getSubReqID()));
        }

    }

    private SubscribeResp resp(int subReqID) {
        Builder builder = SubscribeResp.newBuilder();
        builder.setSubReqID(subReqID);
        builder.setRespCode(0);
        builder.setDesc("netty book order succeed, 3 days later, sent to designated address");
        return builder.build();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

    }
}
