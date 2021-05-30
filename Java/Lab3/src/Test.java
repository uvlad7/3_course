import java.text.NumberFormat;
import java.util.Locale;

public class Test {
    public static String test(double value) {
        return NumberFormat.getInstance(Locale.getDefault()).format(value);
    }

    public static String reverse(String value) {
        return new StringBuilder(value).reverse().toString();
    }

    public static void inf() {
        while (true) {
        }
    }

    public static String test(int a, int b) {
        return "-1";
    }

    public static String test(Integer a, Integer b) {
        return "0";
    }

    public static String test(Integer a, Double b) {
        return "1";
    }

    public static String test(Double a, Integer b) {
        return "2";
    }

    public static String test(Double a, Double b) {
        return "3";
    }
}
