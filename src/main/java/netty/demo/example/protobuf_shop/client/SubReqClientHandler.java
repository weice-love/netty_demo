package netty.demo.example.protobuf_shop.client;

import netty.demo.example.protobuf_shop.entity.SubscribeReqProto.SubscribeReq;
import netty.demo.example.protobuf_shop.entity.SubscribeReqProto.SubscribeReq.Builder;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author DIDIBABA_CAR_QPW Create in 2020/6/8 16:46
 */
public class SubReqClientHandler extends ChannelHandlerAdapter {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("error: " + cause.getMessage());
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < 10; i++) {
            ctx.write(subReq(i));
        }
        ctx.flush();
    }

    private SubscribeReq subReq(int i) {
        Builder builder = SubscribeReq.newBuilder();
        builder.setProductName("计算机");
        builder.setUserName("anruoxin");
        builder.setSubReqID(i);
        builder.setAddress("杭州市余杭区人工智能小镇8幢705");
        return builder.build();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("receive server response : " + msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
