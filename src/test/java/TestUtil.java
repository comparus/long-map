import java.util.Random;
import java.util.function.BiFunction;

public class TestUtil {
    public static final int BOUND = 32000;
    private static final Random RANDOM = new Random();

    public static <T> void apply(int amount, int bound, BiFunction<Long, Integer, T> operator) {
        for (int i = 0; i < amount; i++) {
            long key = RANDOM.nextLong();
            int value = RANDOM.nextInt(bound);

            operator.apply(key, value);
        }
    }
}