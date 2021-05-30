import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("n: ");
        int n = scanner.nextInt();
        Matrix matrix = new Matrix(n, n);
        matrix.fill(-n, n);
        System.out.println("Matrix:");
        matrix.print(System.out);
        System.out.println("Minimums:");
        System.out.println(matrix.countMinimums());
    }
}
