package netty.demo.agreement;

import java.util.HashMap;
import java.util.Map;
import lombok.ToString;

/**
 * @author DIDIBABA_CAR_QPW Create in 2020/6/11 11:20
 */
@ToString
public final class Header {

    /** 消息的校验码 = 0xABEF（固定值，表明该消息是Netty协议消息，2字节） + 主版本号（1-255， 1个字节） + 次版本号（1-255， 1个字节） */
    private int crcCode = 0xabef0101;

    /** 消息长度 */
    private int length;

    /** 会话id */
    private long sessionID;

    /** 0：业务请求消息；1：业务响应消息；2：业务ONE WAY消息（即使请求，也是响应）；3：握手请求；4：握手应答；5：心跳请求；6心跳应答 */
    private byte type;

    /** 消息优先级：0-255 */
    private byte priority;

    /** 附件，用于扩展消息头 */
    private Map<String, Object> attachment = new HashMap<>();

    public final int getCrcCode() {
        return crcCode;
    }

    public final void setCrcCode(int crcCode) {
        this.crcCode = crcCode;
    }

    public final int getLength() {
        return length;
    }

    public final void setLength(int length) {
        this.length = length;
    }

    public final long getSessionID() {
        return sessionID;
    }

    public final void setSessionID(long sessionID) {
        this.sessionID = sessionID;
    }

    public final byte getType() {
        return type;
    }

    public final void setType(byte type) {
        this.type = type;
    }

    public final byte getPriority() {
        return priority;
    }

    public final void setPriority(byte priority) {
        this.priority = priority;
    }

    public final Map<String, Object> getAttachment() {
        return attachment;
    }

    public final void setAttachment(Map<String, Object> attachment) {
        this.attachment = attachment;
    }
}
