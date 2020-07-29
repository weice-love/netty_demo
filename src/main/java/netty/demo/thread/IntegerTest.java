package netty.demo.thread;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author DIDIBABA_CAR_QPW Create in 2020/6/15 16:36
 */
public class IntegerTest {

    AtomicInteger atomicInteger = new AtomicInteger(10000);

    public static void main(String[] args) {

        IntegerTest integerTest = new IntegerTest();
        for (int i = 0; i < 100; i++) {
            new Thread(new Decrement(integerTest), "thread - " + i).start();
        }

        for (;;){

        }

    }

    public void release() {
        for (;;) {
            int tmp = atomicInteger.get();
            System.out.println("当前值: " + tmp);
            if (tmp == 0) {
                System.out.println("结束了！");
                return;
            }
            if (atomicInteger.compareAndSet(tmp, tmp - 1)) {
                if (tmp == 1) {
                    System.out.println("==========================到1了");
                    return;
                }
            }
        }
    }

    static class Decrement implements Runnable {

        private IntegerTest integerTest;

        public Decrement(IntegerTest integerTest) {
            this.integerTest = integerTest;
        }

        @Override
        public void run() {
            integerTest.release();
        }
    }
}
