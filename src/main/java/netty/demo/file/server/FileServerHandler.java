package netty.demo.file.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import java.io.File;
import java.io.RandomAccessFile;

/**
 * @author DIDIBABA_CAR_QPW Create in 2020/6/10 14:10
 */
public class FileServerHandler extends SimpleChannelInboundHandler<String> {

    private static final String CR = System.getProperty("line.separator");

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
        File file = new File(msg);
        if (file.exists()) {
            if (!file.isFile()) {
                ctx.writeAndFlush("Not a file: " + file + CR);
                return;
            }
            ctx.writeAndFlush(file + " " + file.length() + CR);
            RandomAccessFile randomAccessFile = new RandomAccessFile(msg, "r");
            // 通过DefaultFileRegion 进行文件传输
            // FileChannel : 文件通道
            // Position : 文件操作的指针位置
            // Count : 文件总字节数
            // 零拷贝技术
            FileRegion region = new DefaultFileRegion(randomAccessFile.getChannel(), 0,
                    randomAccessFile.length());
            ctx.write(region);
            ctx.writeAndFlush(CR);
            randomAccessFile.close();
        } else {
            ctx.writeAndFlush("Not a file: " + file + CR);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("error: " + cause.getMessage());
        ctx.close();
    }
}
