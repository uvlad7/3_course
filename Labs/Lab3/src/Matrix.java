import java.io.PrintStream;
import java.util.Random;

public class Matrix {
    private int[][] matrix;
    private int row;
    private int col;

    public Matrix(int row, int col) throws NegativeArraySizeException {
        if ((row < 0) || (col < 0)) {
            throw new NegativeArraySizeException("Negative size");
        }
        this.row = row;
        this.col = col;
        this.matrix = new int[this.row][this.col];
    }

    public Matrix() {
        this(0, 0);
    }

    public Matrix(Matrix right) {
        this.row = right.row;
        this.col = right.col;
        this.matrix = new int[this.row][this.col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                this.matrix[i][j] = right.matrix[i][j];
            }
        }
    }

    public void fill(int min, int max) {
        Random random = new Random();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                matrix[i][j] = random.nextInt(max - min + 1) + min;
            }
        }
    }

    public int countMinimums() {
        int n = 0;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (isMinimum(i, j)) {
                    n++;
                }
            }
        }
        return n;
    }

    private boolean isMinimum(int y, int x) {
        for (int i = Math.max(y - 1, 0); i < Math.min(y + 2, row); i++) {
            for (int j = Math.max(x - 1, 0); j < Math.min(x + 2, col); j++) {
                if (matrix[i][j] >= matrix[y][x] && !((i == y) && (j == x))) {
                    return false;
                }
            }
        }
        return true;
    }

    public void print(PrintStream output) {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                output.format("%+d\t", matrix[i][j]);
            }
            output.println();
        }
    }
}
