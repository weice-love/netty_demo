package netty.demo.agreement;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.jboss.marshalling.Marshaller;

/**
 * @author DIDIBABA_CAR_QPW Create in 2020/6/11 13:41
 */
public class MarshallingEncoder {

    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

    Marshaller marshaller;

    public MarshallingEncoder() {
        try {
            marshaller = MarshallingCodeCFactory.buildMarshalling();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void encode(Object msg, ByteBuf out) throws IOException {
        try {
            int lengthPos = out.writerIndex();
            out.writeBytes(LENGTH_PLACEHOLDER);
            ChannelBufferByteOutput output = new ChannelBufferByteOutput(out);
            marshaller.start(output);
            marshaller.writeObject(msg);
            marshaller.finish();
            out.setInt(lengthPos, out.writerIndex() - lengthPos - 4);
        } finally {
            marshaller.close();
        }
    }
}
