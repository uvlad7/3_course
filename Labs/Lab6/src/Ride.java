import java.util.Objects;
import java.util.ResourceBundle;

public class Ride extends Entity {
    private String from;
    private String to;
    private boolean completed;
    private Driver driver;
    private Car car;

    public Ride(ResourceBundle resources, String from, String to, boolean completed, Driver driver, Car car) {
        super(Type.RIDE, resources);
        this.from = from;
        this.to = to;
        this.completed = completed;
        this.driver = driver;
        this.car = car;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ride)) return false;
        Ride ride = (Ride) o;
        return from.equals(ride.from) &&
                to.equals(ride.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(resources.getString("ride"));
        sb.append("{").append(resources.getString("created")).append(": ").append(getCreationDate());
        sb.append(", ").append(resources.getString("from")).append(": ").append(from);
        sb.append(", ").append(resources.getString("to")).append(": ").append(to);
        sb.append(", ").append(resources.getString("completed")).append(": ");
        sb.append(resources.getString(Boolean.toString(completed)));
        sb.append(", ").append(resources.getString("driver")).append(": ").append(driver.getSurname()).append(' ').append(driver.getName());
        sb.append(", ").append(resources.getString("car")).append(": ").append(car.getNumber()).append('}');
        return sb.toString();
    }
}
