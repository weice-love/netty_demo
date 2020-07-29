package netty.demo.agreement;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.jboss.marshalling.ByteInput;
import org.jboss.marshalling.Unmarshaller;

/**
 * @author DIDIBABA_CAR_QPW Create in 2020/6/11 14:11
 */
public class MarshallingDecoder {

    Unmarshaller unmarshaller;

    public MarshallingDecoder() {
        try {
            unmarshaller = MarshallingCodeCFactory.buildUnMarshalling();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object decode(ByteBuf in) throws Exception {

        int objSize = in.readInt();
        ByteBuf buf = in.slice(in.readerIndex(), objSize);
        ByteInput input = new ChannelBufferByteInput(buf);

        try {
            unmarshaller.start(input);
            Object obj = unmarshaller.readObject();
            unmarshaller.finish();
            // 读取完之后设置读取的位置
            in.readerIndex(in.readerIndex() + objSize);
            return obj;
        } finally {
            // Call close in a finally block as the ReplayingDecoder will throw an Error if not enough bytes are
            // readable. This helps to be sure that we do not leak resource
            unmarshaller.close();
        }
    }

}
