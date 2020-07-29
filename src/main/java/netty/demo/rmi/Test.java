package netty.demo.rmi;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import javax.xml.bind.Marshaller;

/**
 * @author DIDIBABA_CAR_QPW Create in 2020/6/29 17:25
 */
public class Test {

    static Predicate<Integer> test = v -> v % 2 == 0;
    static Predicate<Integer> test1new = new Predicate<Integer>() {
        @Override
        public boolean test(Integer a) {
            return a % 2 == 0;
        }
    };

    public static void main(String[] args) {
        System.out.println(8L);
        System.out.println(object2Long(Long.valueOf(8L)));
        Map<String, Object> a = new HashMap<>();
        a.put("a", 10909L);
        Long b = 90310308L;
        a.put("b", b);
        System.out.println(Long.valueOf(a.get("a").toString()));
        System.out.println((long)a.get("a"));
        System.out.println((long)a.get("b"));
    }

    public static Long object2Long(Object a) {
        return (Long) a;
    }

    public static int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

}
