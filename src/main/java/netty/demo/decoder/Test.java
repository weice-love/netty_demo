package netty.demo.decoder;

/**
 * @author DIDIBABA_CAR_QPW Create in 2020/6/8 14:51
 */
public class Test {

    public static void main(String[] args) {
        // 应用协议为了对消息进行区分，采用如下四种方式
        // 1. 消息长度固定，累计读取到长度总和为LEN的报文后，就认为读取到了一个完整的消息，将计数器置位，重新开始读取下一个数据报
        // 2. 将回车换行符作为消息结束符
        // 3. 将特殊的分隔符作为消息的结束标志
        // 4. 通过在消息头中定义长度字段来标识消息的长度
    }
}
