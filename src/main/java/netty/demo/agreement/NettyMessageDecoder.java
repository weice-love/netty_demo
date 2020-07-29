package netty.demo.agreement;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 继承LengthFieldBasedFrameDecoder， 支持自动的TCP粘包和半包处理，只需要给出标识消息的长度的字段偏移量和消息长度自身所占的
 * 字节数，Netty就能自动实现对半包的处理。
 * @author DIDIBABA_CAR_QPW Create in 2020/6/11 11:26
 */
public final class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {

    private MarshallingDecoder marshallingDecoder;

    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        marshallingDecoder = new MarshallingDecoder();
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        // 为空说明是半包消息
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            System.out.println("NettyMessageDecoder NULL");
            return null;
        }
        System.out.println("frame: " + frame.toString());

        // todo ？？？ 按理应该用frame 的
        in.readerIndex(0);
        NettyMessage nettyMessage = new NettyMessage();
        Header header = new Header();
        header.setCrcCode(in.readInt());
        header.setLength(in.readInt());
        header.setSessionID(in.readLong());
        header.setType(in.readByte());
        header.setPriority(in.readByte());
        int size = in.readInt();
        if (size > 0) {
            Map<String, Object> attachment = new HashMap<>();
            int keySize = 0;
            byte[] keyArray = null;
            String key = null;
            for (int i = 0; i < size; i++) {
                keySize = in.readInt();
                keyArray = new byte[keySize];
                in.readBytes(keyArray);
                key = new String(keyArray, StandardCharsets.UTF_8);
                attachment.put(key, marshallingDecoder.decode(in));
            }
            keyArray = null;
            key = null;
            header.setAttachment(attachment);
        }
        if (in.readableBytes() > 4) {
            nettyMessage.setBody(marshallingDecoder.decode(in));
        }
        nettyMessage.setHeader(header);
        return nettyMessage;
    }
}
