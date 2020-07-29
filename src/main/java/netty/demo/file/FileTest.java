package netty.demo.file;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author DIDIBABA_CAR_QPW Create in 2020/6/9 20:26
 */
public class FileTest {

    public static void main(String[] args) throws IOException {
        // 文件 计算机中一种基本的数据存储形式
        fileChannelTest();
    }

    public static void fileChannelTest() throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile("c://anruoxin//test_data//2.txt", "rw");
        FileChannel channel = randomAccessFile.getChannel();
        String content = "aaaaa中国浙江省杭州市余杭区中国人工智能小镇8幢1";
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put(content.getBytes());
        byteBuffer.flip();
        channel.write(byteBuffer);
        channel.close();
    }
}
