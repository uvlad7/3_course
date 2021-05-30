import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Seller extends Employee {
    private static double CONST_PROC = 4;
    private double revenue;

    public Seller(String surname, String organization, double coefficient, double revenue) throws BadInputException {
        super(surname, organization, coefficient);
        if (revenue < 0)
            throw new BadInputException("Revenue of caffeine can't be less than 0");
        this.revenue = revenue;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Seller, revenue = ");
        sb.append(revenue).append(", ").append(super.toString());
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Seller)) return false;
        if (!super.equals(o)) return false;
        Seller seller = (Seller) o;
        return Double.compare(seller.revenue, revenue) == 0;
    }

    public double getRevenue() {
        return revenue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), revenue);
    }

    @Override
    public double salary() {
        return new BigDecimal(getCoefficient() * revenue * CONST_PROC / 100).setScale(2, RoundingMode.UP).doubleValue();
    }
}
