package netty.demo.netty_zhanbao.handler;

import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * 粘包的解决方法
 *  1. 消息定长，例如每个报文的大小固定长度为200字节，不够空格补位（定长）
 *  2. 在包尾增加回车换行符进行分割（FTP协议）
 *  3. 将消息分为消息头和消息尾，消息头中包含表示消息的总长度的字段，通常设计思路为消息头的第一个字段使用int32来表示消息的总长度
 *  4. 更复杂的应用层协议
 * @author DIDIBABA_CAR_QPW Create in 2020/6/8 14:31
 */
public class DecoderTest {

    public static void main(String[] args) {
        // 工作原理 （他是以换行符位结束标志的解码器，支持携带换行符和不携带换行符两种编码，同时支持配置单行的最大长度）
        // 依次遍历ByteBuf中可读的字节，判断看是否有“\n”, “\r\n”， 如果有，就以此位结束位置，从可读索引到
        // 结束位置区间的字节就组成了一行。
        //如果连续读取到最大长度后仍然没有发现换行符，就会抛出异常，同时忽略之前读到的异常码流。
        LineBasedFrameDecoder lineBasedFrameDecoder = new LineBasedFrameDecoder(1024);

        // 将接收的对象转换成字符串，然后继续调用后面的handler
        StringDecoder stringDecoder = new StringDecoder();

    }

}
