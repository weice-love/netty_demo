package netty.demo.agreement;

/**
 * @author DIDIBABA_CAR_QPW Create in 2020/6/11 14:44
 */
public enum MessageType {

    LOGIN_REQ((byte)3),
    LOGIN_RESP((byte)4),
    HEARTBEAT_REQ((byte)5),
    HEARTBEAT_RESP((byte)6);

    private byte value;

    MessageType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
