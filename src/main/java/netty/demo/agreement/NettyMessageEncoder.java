package netty.demo.agreement;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map.Entry;

/**
 * @author DIDIBABA_CAR_QPW Create in 2020/6/11 11:26
 */
public final class NettyMessageEncoder extends MessageToMessageEncoder<NettyMessage> {

    private MarshallingEncoder marshallingEncoder;

    public NettyMessageEncoder() {
        this.marshallingEncoder = new MarshallingEncoder();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, NettyMessage msg, List<Object> out) throws Exception {
        if (msg == null || msg.getHeader() == null) {
            throw new Exception("The encode message is null");
        }
        ByteBuf sendBuffer = Unpooled.buffer();
        sendBuffer.writeInt(msg.getHeader().getCrcCode());
        sendBuffer.writeInt(msg.getHeader().getLength());
        sendBuffer.writeLong(msg.getHeader().getSessionID());
        sendBuffer.writeByte(msg.getHeader().getType());
        sendBuffer.writeByte(msg.getHeader().getPriority());
        sendBuffer.writeInt(msg.getHeader().getAttachment().size());

        String key = null;
        byte[] keyArray = null;
        Object value;
        for (Entry<String, Object> param : msg.getHeader().getAttachment().entrySet()) {
            key = param.getKey();
            keyArray = key.getBytes(StandardCharsets.UTF_8);
            sendBuffer.writeInt(keyArray.length);
            sendBuffer.writeBytes(keyArray);
            value = param.getValue();
            marshallingEncoder.encode(value, sendBuffer);
        }
        key = null;
        keyArray = null;
        value = null;
        if (msg.getBody() != null) {
            marshallingEncoder.encode(msg.getBody(), sendBuffer);
        } else {
            sendBuffer.writeInt(0);
            sendBuffer.setInt(4, sendBuffer.readableBytes());
        }
        out.add(sendBuffer);
    }
}
