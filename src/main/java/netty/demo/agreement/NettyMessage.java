package netty.demo.agreement;

import lombok.ToString;

/**
 * @author DIDIBABA_CAR_QPW Create in 2020/6/11 11:19
 */
@ToString
public final class NettyMessage {

    /** 消息头 */
    private Header header;

    /** 消息体 */
    private Object body;

    public final Header getHeader() {
        return header;
    }

    public final void setHeader(Header header) {
        this.header = header;
    }

    public final Object getBody() {
        return body;
    }

    public final void setBody(Object body) {
        this.body = body;
    }
}
