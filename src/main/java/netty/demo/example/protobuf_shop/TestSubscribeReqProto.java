package netty.demo.example.protobuf_shop;

import netty.demo.example.protobuf_shop.entity.SubscribeReqProto;
import netty.demo.example.protobuf_shop.entity.SubscribeReqProto.SubscribeReq;
import netty.demo.example.protobuf_shop.entity.SubscribeReqProto.SubscribeReq.Builder;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @author DIDIBABA_CAR_QPW Create in 2020/6/8 17:10
 */
public class TestSubscribeReqProto {

    private static  byte[] encode(SubscribeReq req) {
        return req.toByteArray();
    }

    private static SubscribeReq decode(byte[] body) throws InvalidProtocolBufferException {
        return SubscribeReq.parseFrom(body);
    }

    private static SubscribeReqProto.SubscribeReq createSubscribeReq() {
        Builder builder = SubscribeReq.newBuilder();
        builder.setSubReqID(1);
        builder.setUserName("anruoxin");
        builder.setProductName("netty项目");
        builder.setAddress("杭州市余杭区中国人工只能小镇");
        return builder.build();
    }

    public static void main(String[] args) throws InvalidProtocolBufferException {
        SubscribeReq subscribeReq = createSubscribeReq();
        System.out.println("before encode : " + subscribeReq.toString());
        SubscribeReq subscribeReq1 = decode(encode(subscribeReq));
        System.out.println("after decode : " + subscribeReq1.toString());
        System.out.println("assert equal : " + subscribeReq.equals(subscribeReq1));

    }
}
