import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        Ball[] array = new Ball[]{new Ball(10, 12, 13, 2), new Ball(),
                new Ball(9, 5, 7, 13), new Ball(14.6, 55.7, 34.5, 1),
                new Ball("Ball{r = 10, x = 12, y = 13, z = 2}"),
                new Ball("Ball{r = 19.3, x = 12.6, y = 1, z = 0.2}")};
        System.out.println(Arrays.toString(array));
        System.out.println("expect true: " + array[0].equals(array[4]));
        System.out.println("expect true: " + (array[0].hashCode() == array[4].hashCode()));
        while (array[0].hasNext()) {
            System.out.print(array[0].next() + " ");
        }
        System.out.println();
        System.out.println("Area: " + array[0].area());
        System.out.println("Volume: " + array[0].volume());
        Arrays.sort(array);
        System.out.println(Arrays.toString(array));
    }
}
