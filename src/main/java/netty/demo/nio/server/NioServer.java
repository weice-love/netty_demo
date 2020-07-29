package netty.demo.nio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

/**
 * @author DIDIBABA_CAR_QPW Create in 2020/6/16 10:45
 */
public class NioServer {

    private static final int PORT = 9999;


    static class TimeServer implements Runnable {

        private Selector selector;

        private ServerSocketChannel serverSocketChannel;

        private volatile boolean stop;

        public TimeServer(int port) throws IOException {

            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(port), 1024);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        }

        @Override
        public void run() {
            while (!stop) {

            }

        }
    }

}
