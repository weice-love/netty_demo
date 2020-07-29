package netty.demo.serialize;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import lombok.Data;

/**
 * @author DIDIBABA_CAR_QPW Create in 2020/6/8 15:46
 */
public class StudentTest {

    public static void main(String[] args) throws IOException {

        Student student = new Student(100, "xiaoming", "三年级一班");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(student);
        objectOutputStream.flush();
        objectOutputStream.close();
        byte[] bytes = byteArrayOutputStream.toByteArray();
        System.out.println("the jdk serializable length is : " + bytes.length);
        byteArrayOutputStream.close();
        System.out.println("the byte array length is : " + student.codeC().length);
    }

    @Data
    static class Student implements Serializable {

        private static final long serialVersionUID = -7709073607484023549L;

        private Integer age;
        private String name;
        private String className;

        public Student(Integer age, String name, String className) {
            this.age = age;
            this.name = name;
            this.className = className;
        }

        public byte[] codeC() {
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            byteBuffer.putInt(this.getAge());
            byte[] bytes = this.getName().getBytes();
            byteBuffer.putInt(bytes.length);
            byteBuffer.put(bytes);
            bytes = this.getClassName().getBytes();
            byteBuffer.putInt(bytes.length);
            byteBuffer.put(bytes);
            byteBuffer.flip();
            bytes = null;
            byte[] result = new byte[byteBuffer.remaining()];
            byteBuffer.get(result);
            return result;

        }

        public byte[] codeC(ByteBuffer byteBuffer) {
            byteBuffer.putInt(this.getAge());
            byte[] bytes = this.getName().getBytes();
            byteBuffer.putInt(bytes.length);
            byteBuffer.put(bytes);
            bytes = this.getClassName().getBytes();
            byteBuffer.putInt(bytes.length);
            byteBuffer.put(bytes);
            byteBuffer.flip();
            bytes = null;
            byte[] result = new byte[byteBuffer.remaining()];
            byteBuffer.get(result);
            return result;
        }
    }

}
