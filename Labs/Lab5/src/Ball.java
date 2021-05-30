import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ball implements Body, Comparable<Body>, Iterator<Double> {
    private static Pattern pattern = Pattern.compile("^Ball\\{r = ([-+]?\\d+(?:\\.\\d+)?), x = ([-+]?\\d+(?:\\.\\d+)?), y = ([-+]?\\d+(?:\\.\\d+)?), z = ([-+]?\\d+(?:\\.\\d+)?)}$");

    private Double r, x, y, z;
    private int pos;


    public Ball() {
        this(0, 0, 0, 0);
    }

    public Ball(double r, double x, double y, double z) {
        setR(r);
        setX(x);
        setY(y);
        setZ(z);
        pos = -1;
    }

    public Ball(String def) {
        Matcher matcher = pattern.matcher(def);
        if (!matcher.matches()) {
            throw new NumberFormatException("Incorrect string format");
        }
        setR(Double.parseDouble(matcher.group(1)));
        setX(Double.parseDouble(matcher.group(2)));
        setY(Double.parseDouble(matcher.group(3)));
        setZ(Double.parseDouble(matcher.group(4)));
        pos = -1;
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getZ() {
        return z;
    }

    public void setZ(Double z) {
        this.z = z;
    }

    @Override
    public double area() {
        return 4 * Math.PI * r * r;
    }

    @Override
    public double volume() {
        return 4.0 / 3.0 * Math.PI * r * r * r;
    }

    @Override
    public int compareTo(Body o) {
        return Double.compare(volume(), o.volume());
    }

    @Override
    public boolean hasNext() {
        return (pos < 3);
    }

    @Override
    public Double next() {
        pos++;
        switch (pos) {
            case 0: {
                return r;
            }
            case 1: {
                return x;
            }
            case 2: {
                return y;
            }
            case 3: {
                return z;
            }
            default: {
                throw new NoSuchElementException("No more fields");
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ball)) return false;
        Ball ball = (Ball) o;
        return r.equals(ball.r) &&
                x.equals(ball.x) &&
                y.equals(ball.y) &&
                z.equals(ball.z);
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, x, y, z);
    }

    @Override
    public String toString() {
        return String.format("Ball{r = %s, x = %s, y = %s, z = %s}", r, x, y, z);
    }
}
