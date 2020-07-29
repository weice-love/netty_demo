package netty.demo.websocket;

/**
 * @author DIDIBABA_CAR_QPW Create in 2020/6/9 16:00
 */
public class WebSocketTest {

    public static void main(String[] args) {
        // http协议弊端
        // 1. 半双工协议，不能同时传输
        // 2. 消息冗长二繁琐
        // 3. 针对服务器推送的黑客攻击，长时间轮询

        // websocket特点
        // 1. 单一的TCP连接，采用全双工模式通信
        // 2. 对代理，防火墙和路由器透明
        // 3. 无头部信息，Cookies和身份验证
        // 4. 无安全开销
        // 5. 通过"ping/pong"帧保持链路激活
        // 6. 服务器可以主动传递消息给客户端，不再需要客户端轮询
    }

}
