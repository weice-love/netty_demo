package netty.demo.agreement;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 协议
 *
 * 私有协议也称非标准协议 （厂商内部发展和采用的标准）
 *  封闭性， 排他性， 垄断性
 *
 * 传统JAVA项目，通常采用以下四种方式进行跨节点通信
 *  1. 通过RMI（remote method invoke）进行远程服务调用
 *  2. 通过JAVA的Socket+JAVA序列化的方式进行跨节点调用
 *  3. 利用一些开源的RPC框架进行远程服务调用，例如Facebook的Thrift
 *  4. 利用标准的公有协议进行跨节点服务调用，例如HTTP+XML.RESTFUL+JSON或者WebService
 *
 *        netty
 *          |
 *          |
 *          |
 *        交换机
 *        /   \
 *       /     \
 *      /       \
 *   netty <---> netty
 *         netty协议
 *
 *   功能
 *      1. 基于Netty的NIO通信框架，提供高性能的异步通信能力
 *      2. 提供消息的编解码框架，可以实现POJO的序列话和反序列化能力
 *      3. 提供基于IP地址的白名单接入认证机制
 *      4. 链路的有效性校验机制
 *      5. 链路的断连重传机制
 *
 *   通信模型
 *
 *   客户端                                   服务端
 *            ---------------> 1. 握手请求
 *            <--------------- 2. 握手应答
 *            ---------------> 3. 发送业务消息
 *            <--------------- 4. 发送心跳消息
 *            ---------------> 5. 发送心跳消息
 *            <--------------- 6. 发送业务消息
 *    1. 客户端发送握手请求消息，携带节点ID等有效身份认证信息
 *    2. 服务端对握手请求消息进行合法性校验，包括节点ID有效性校验，节点重复登录校验和IP地址合法性校验，校验通过后，返回登录成功的握手应答消息
 *    3. 链路建立成功之后，客户端发送业务消息
 *    4. 链路成功之后，服务端发送心跳消息
 *    5. 链路建立成功之后，客户端发送心跳消息
 *    6. 链路建立成功之后，服务端发送业务消息
 *
 *    全双工，双方之间的心跳采用ping-pong机制，链路空闲时，客户端ping，服务端pong，没有收到pong，链路异常，客户端主动关闭连接，间隔周期T后发起重连，知道重连成功
 *
 * Netty协议栈 - 消息定义
 *  消息定义表
 *      名称          类型        长度          描述
 *      header       Header     变长          消息头定义
 *      body         Object     变长          对于请求消息，他是方法的参数；对于响应消息，他是返回值
 *  消息头定义
 *      crcCode      int        32           消息的校验码 = 0xABEF（固定值，表明该消息是Netty协议消息，2字节） + 主版本号（1-255， 1个字节） + 次版本号（1-255， 1个字节）
 *      length       int        32           整个消息长度，包括消息头和消息体
 *      sessionID    long       64           集群节点内全局唯一，由会话ID生成器生成
 *      type         Byte       8            0：业务请求消息；1：业务响应消息；2：业务ONE WAY消息（即使请求，也是响应）；3：握手请求；4：握手应答；5：心跳请求；6心跳应答
 *      priority     Byte       8            消息优先级：0-255
 *      attachment   Map<String, Object>    变长 可选字段，用于扩展消息头
 *
 *
 *  Netty协议支持的数据类型
 *      大部分
 *
 *  Netty协议的编解码规范
 *      crcCode: java.nio.ByteBuffer.putInt(int value) , 如果采用其他缓冲区实现，必须与其等价
 *      length: java.nio.ByteBuffer.putInt(int value) , 如果采用其他缓冲区实现，必须与其等价
 *      sessionID: java.nio.ByteBuffer.putLong(long value) , 如果采用其他缓冲区实现，必须与其等价
 *      type: java.nio.ByteBuffer.put(byte value) , 如果采用其他缓冲区实现，必须与其等价
 *      priority: java.nio.ByteBuffer.put(byte value) , 如果采用其他缓冲区实现，必须与其等价
 *      attachment:
 *          长度为0：没有可选附件，长度编码设置为0，java.nio.ByteBuffer.putInt(0)
 *          长度大于0：存在可选附件，需要编码
 *              1. 对附件个数进行编码 java.nio.ByteBuffer.putInt(attachment.size())
 *              2. 对key进行编码，先编码长度，在将他转换成byte数组之后编码内容
 *      body编码：通过插件序列化byte数组，在调用java.nio.ByteBuffer.put(byte[] value)，
 *          由于整个消息的长度必须当全部字段都编码完成之后才能确认，所以最后需要更新消息头中的length字段，将其重新写入ByteBuffer中
 *
 *  Netty协议的解码
 *      crcCode: java.nio.ByteBuffer.getInt() 获取校验字段，其他缓冲区实现，必须与其等价
 *      length: java.nio.ByteBuffer.getInt() 获取Netty消息长度, 如果采用其他缓冲区实现，必须与其等价
 *      sessionID: java.nio.ByteBuffer.getLong() 获取会话ID, 如果采用其他缓冲区实现，必须与其等价
 *      type: java.nio.ByteBuffer.get() 获取消息类型, 如果采用其他缓冲区实现，必须与其等价
 *      priority: java.nio.ByteBuffer.get() 获取优先级, 如果采用其他缓冲区实现，必须与其等价
 *      attachment:
 *          创建一个attachment对象，通过java.nio.ByteBuffer.getInt()获取附件长度，如果为0,说明附件为空，解码结束。
 *          不为空，则根据长度进行for循环解码
 *
 * 链路的建立
 *  Netty协议栈对客户端的说明
 *      如果A节点需要调用B节点的服务，但是A和B之间还没有建立物理链路，则由调用方主动发起连接，此时调用方为客户端。
 *  安全考虑
 *      通过基于IP地址或者号段的黑白名单安全认证机制，商用中会更加严格（通过密钥对用户名和密码进行安全认证）
 *  客户端与服务端链路建立之后，客户端发送握手请求消息，定义如下
 *      1. 消息头的type字段值为3
 *      2. 可选附件个数为0
 *      3. 消息体为空
 *      4. 握手消息的长度为22字节
 *  服务端接收到客户端的握手请求消息之后，如果IP校验通过，返回握手成功应答消息，定义如下
 *      1. 消息头的type为4
 *      2. 可选附件个数为0
 *      3. 消息体为type类型的结果：0： 认证成功； -1 ： 认证失败
 *
 * 链路的关闭
 *  采用长链接，双方通过心跳和业务消息维持链路，不需要关闭
 *  存在以下情况，需要关闭连接
 *      1. 当对方宕机或者重启，会主动关闭链路，另一方读取操作系统的通知信号，得知对方REST链路，需要关闭连接，释放自身的句柄等资源。由于采用TCP全双工通信，
 *          通信双方都需要关闭连接，释放资源
 *      2. 消息读写过程中，发生了I/O异常，需要主动关闭连接
 *      3. 心跳消息读写过程中发生了I/O异常，需要主动关闭连接
 *      4. 心跳超时，需要主动关闭连接
 *      5. 发生编码异常等不可恢复错误时，需要主动关闭连接。
 *==============================================================================================================================
 * 可靠性设计 =
 *=============
 *  1. 心跳机制（业务低谷时期，网络闪断，超时，由于没有业务消息，应用进程很难发现，到了业务高峰期会发生大量的网络通信失败，导致无法处理业务消息）
 *      设计思路
 *          1. 当网络处于空闲状态持续时间达到T（连续周期T没有读写消息）时，客户端主动发送Ping心跳消息个i服务端
 *          2. 如果在下一个周期T到来时客户端没有收到对方发送的Pong心跳应答消息或者读取到服务端发送的其他业务消息，则心跳失败计数器加1
 *          3. 每当客户端接收到服务端的业务消息或者Pong应答消息，将心跳失败计数器清零；当连续N次没有接收到服务端的Pong应答消息或者业务消息，则关闭链路；间隔
 *              INTERVAL时间后发起重连操作
 *          4. 服务端网络空闲状态持续时间达到T后，服务端将心跳失败计数器加1；只要接收到客户端发送的Ping消息或者其他业务消息，计数器清零。
 *          5. 服务端连续N次没有接收到客户端的Ping消息或者其他业务消息，则关闭链路，释放资源，等待客户重连
 *          说明:
 *              通过ping-pong双向心跳机制，可以保证无论通信哪一方出现网络故障，都能被及时检测出来，为了防止由于对方短时间内繁忙没有及时返回应答造成
 *              的误判，只有连续N次心跳检测都失败了才认定链路已经损坏，需要关闭连续并重建链路
 *
 *              当读或写心跳消息发生I/O异常的时候，说明链路已经中断，此时需要立即关闭链路，如果是客户端，需要重新发起连接，如果是服务端，需要清空
 *              缓存的半包消息，等待客户端重连
 *
 * 重连机制
 *  如果链路中断，等待INTERVAL时间后，由客户端发起重连操作，如果重连失败，间隔周期INTERVAL时间之后再发起重连，知道重连成功
 *  原因： 保证服务端由充足的时间释放句柄资源
 *  要点：客户端必须保证自身的资源被及时释放，包括但不限于SocketChannel，Socket等。重连失败，需要打印堆栈信息，方便后续定位
 *
 * 重复登录保护
 *  当客户端登陆成功之后，不允许客户端重复登入，以防止客户端在异常状态下反复重连导致句柄资源被耗尽（不能多次登录成功，需要拒绝重复登入）
 *  服务端接收到客户端的握手请求消息之后，首先对IP地址进行合法性校验，如果校验成功，在缓存的地址表中查看客户端是否已经登录，如果已经登录，则拒绝重复登录，
 *  返回错误码-1，同时关闭链路，并在服务端的日志中打印握手失败的原因。
 *
 *  失败之后，重连（同上）
 *
 *  为了防止由服务端对链路状态理解不一致导致客户端无法握手成功，当服务端连续N次心跳超时之后需要主动关闭链路，清空该客户端的地址缓存信息，以保证后续的客户端
 *  可以重连成功，防止被重复登录保护机制拒绝掉
 *
 * 消息缓存重发
 *  无论客户端还是服务端，当发生链路中断之后，在链路恢复之前，缓存在消息队列中待发送的消息不能丢失，等待链路恢复之后，重新发送这些消息，保证链路中断期间消息不会
 *  丢失
 *
 *  考虑内存溢出的风险，建议消息缓存队列设置上限，当达到上限之后，应该拒绝继续向队列添加消息。
 *
 *==============================================================================================================================
 * 安全性设计 =
 * ==========
 *  为了保证整个集群环境的安全，内部长连接采用基于IP地址的安全认证机制，服务端对握手请求消息的IP地址进行合法性校验，如果在白名单之内，则校验通过。
 *
 *  在公网环境下，需要采用更严格的安全认证机制，基于密钥和AES加密的用户名和密码认证机制，也可以采用SSL/TSL安全传输
 *
 * =============================================================================================================================
 * 可扩展性设计 =
 * ============
 *  Netty协议需要具备一定的扩展能力，业务可以在消息头中自定义业务域字段。通过Netty消息头中的可选附件attachment字段，可以方便进行自定义扩展
 *
 *  Netty协议栈架构需要具备一定的扩展能力，例如统一的消息拦截，接口日志，安全，加解密等可以方便地添加或删除，不需要修改之前的逻辑代码，类似Servlet的Filter
 *  Chain 和AOP 但考虑性能因素，不推荐AOP来实现功能的拓展
 *
 * =====================================================================================================================
 * 数据结构定义 =
 * ============
 *
 *
 *
 *
 * @author DIDIBABA_CAR_QPW Create in 2020/6/10 15:21
 */
public class AgreementTest {

    public static void main(String[] args) {
    }

    public static void encode() {
        Map<String, Object> attachment = new HashMap<>();
        String key = null;
        byte[] value = null;
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        for (Entry<String, Object> param : attachment.entrySet()) {
            key = param.getKey();
            byteBuffer.putInt(key.length());
            value = attachment.get(key).toString().getBytes();
            byteBuffer.put(value);
        }
    }

    public static void decode() {
        Map<String, Object> attachment = new HashMap<>();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        int length = byteBuffer.getInt();
        for (int i = 0; i < length; i++) {
            byte[] keyByte = new byte[10];
            byteBuffer.get(keyByte);
            byte[] valueByte = new byte[10];
            byteBuffer.get(valueByte);
            attachment.put(new String(keyByte, StandardCharsets.UTF_8), toObject(valueByte));
        }
    }

    /**
     * 对象转数组
     * @param obj
     * @return
     */
    public static byte[] toByteArray (Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray ();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bytes;
    }

    /**
     * 数组转对象
     * @param bytes
     * @return
     */
    public static Object toObject (byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
            ObjectInputStream ois = new ObjectInputStream (bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return obj;
    }

}
