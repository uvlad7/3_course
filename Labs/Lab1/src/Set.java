import java.util.Locale;

public class Set {
    public static void sumOfSet(String[] args) {
        if (args.length != 2) {
            System.err.println("Invalid number of arguments");
            System.exit(1);
        }
        try {
            double x = Double.parseDouble(args[0]);
            if (x >= 1 || x < -1) {
                System.err.println("Invalid argument: " + x);
                System.exit(1);
            }
            int k = Integer.parseInt(args[1]);
            if (k < 0) {
                System.err.println("Invalid argument: " + k);
                System.exit(1);
            }
            double epsilon = 1;
            for (int i = 0; i < k; i++) {
                epsilon /= 10;
            }
            double sum = 0.0;
            int n = 1;
            double member = -x;
            while (Math.abs(member) >= epsilon) {
                sum += member / n;
                member *= x;
                n++;
            }
            System.out.format(Locale.ENGLISH, "%." + k + "f %n", sum);
        } catch (NumberFormatException e) {
            System.err.println("Invalid arguments");
            System.exit(1);
        }
    }
}
