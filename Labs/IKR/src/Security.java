import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Security extends Employee {
    private static double CONSTANT = 2;
    private static double CONST_BASE = 3;
    private double square;

    public Security(String surname, String organization, double coefficient, double square) throws BadInputException {
        super(surname, organization, coefficient);
        if (square < 0)
            throw new BadInputException("Square of caffeine can't be less than 0");
        this.square = square;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Security, square = ");
        sb.append(square).append(", ").append(super.toString());
        return sb.toString();
    }

    public double getSquare() {
        return square;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Security)) return false;
        if (!super.equals(o)) return false;
        Security security = (Security) o;
        return Double.compare(security.square, square) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), square);
    }

    @Override
    public double salary() {
        return new BigDecimal(CONST_BASE * getCoefficient() * square / CONSTANT).setScale(2, RoundingMode.UP).doubleValue();
    }
}
