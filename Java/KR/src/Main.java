import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

public class Main {
    private static final int[] ARRAY = {0, 0, 0, 0, 0};
    private static final CyclicBarrier BARRIER = new CyclicBarrier(5, new Printer());
    private static AtomicBoolean CONTINUE = new AtomicBoolean(true);

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            new Thread(new Randomizer(i)).start();
        }
    }

    //Таск, который будет выполняться при достижении сторонами барьера
    public static class Printer implements Runnable {
        @Override
        public void run() {
            if (IntStream.of(ARRAY).sum() > 20) {
                System.out.println(Arrays.toString(ARRAY));
                CONTINUE.set(false);
            } else {
                System.err.println(Arrays.toString(ARRAY));
            }
        }
    }

    //Стороны, которые будут достигать барьера
    public static class Randomizer implements Runnable {
        private int number;
        private Random random;

        public Randomizer(int number) {
            this.number = number;
            random = new Random();
        }

        @Override
        public void run() {
            while (CONTINUE.get()) {
                try {
                    ARRAY[number] = random.nextInt(8);
                    BARRIER.await();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }
}